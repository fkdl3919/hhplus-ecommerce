### 개요
### 부하테스트(Load test)
성능테스트의 하위 집합이며 적절한 부하를 발생시켜 통계적으로 의미있는 수치를 측정하는 테스트를 말한다. 시스템의 상한선을 식별할 때 사용되며 서비스의 SLA를 설정하고 시스템이 과부하 볼륨을 처리하는 방법을 확인한다.

현재 이커머스 프로젝트에는 주요 4가지 시나리오가 존재한다.
1. 포인트 충전 및 조회
2. 주문 & 결제
3. 선착순 쿠폰 발급
4. 인기상품 순위 조회 (캐시 적용으로 제외)

인기상품 순위 조회를 제외한 3가지 시나리오 중 `선착순 쿠폰 발급` 시나리오에 부하테스트를 적용할 예정이며 부하테스트를 통해
- 처리 능력 평가
    - API가 감당할 수 있는 최대 요청량과 동시 접속자 수를 측정하여 시스템의 임계점을 파악
- 내구성 검증
    - 서버가 특정 부하조건에서도 지속적으로 정상 운영되는지를 확인
      위 두가지를 결과 지표로 확인할 예정이다.

#### 부하 테스트의 전제조건
보다 정확한 시스템 상한을 식별하기 위해 도커 컨테이너 환경에서 테스트를 진행한다.

```yml
version: '3.7'
services:
  myservice:
    image: myservice:latest
    ports:
      - "8080:8080"
    deploy:
      resources:
        limits:
          cpus: '1.5' // 리소스제한
          memory: 2g  // 리소스제한
```


### 시나리오 분석
#### 포인트 충전 및 조회
한 사람에 의한 포인트 충전 요청은 서비스에 영향이 가거나 부하를 주지 않는다고 판단된다.
이 경우 부하테스트의 개념보다는 정합성 테스트에 가깝다.
n원을 충전 했을 때 정확하게 n원이 충전되었는지 등

테스트 범위
- 동시성 테스트
    - 한명의 사용자가 같은요청을 반복하였을때 충전금액, 조회금액을 정확히 반환하는지


#### 주문 & 결제
이커머스 프로젝트의 메인 시나리오로 여러 도메인들이 결합되어 있는 형태이다.
메인 시나리오인 만큼 일반적인 정상 부하(LOAD) 뿐만 아니라, 특정 이벤트로 인한 추가 부하(PEAK) 상황도 고려해야 하므로 시스템 스케일 아웃을 고려한 테스트를 진행하여야 한다.

테스트 범위
- 부하 테스트
    - 시스템의 트래픽 병목 또는 응답시간이 늦어지는 TPS 측정 (내구성 검증)
    - 정상 부하 상황에서 PEAK 상황을 고려해 감당할 수 있는 최대요청량 측정(처리 능력 평가)

#### 선착순 쿠폰 발급
이벤트 성 시나리오로 선착순 이라는 키워드에 걸맞게 SPIKE 형태의 부하유형을 갖고있다.
단기간(초 단위) 요청이 급증할 수도 있는 시나리오 이기 때문에 내구성보다는 순간적인 트래픽에 반응 할 수 있는지 평가하고 서비스가 보장할 수 있는 SLA를 측정한다. ( e.g. 서비스 장애율 0.1% 미만 .. )

테스트 범위
- 부하 테스트: 부하유형 PEAK
    - 단기간 대량 트래픽(PEAK 상황)을 견딜 수 있도록 시스템의 최대 처리량을 확인 (처리 능력 평가)


### K6 부하테스트

선착순 쿠폰 발급 테스트 - Peak Test ( 최고 부하 테스트 )
```javascript
 
let options = { scenarios: {},thresholds: {  
    // 전체 체크 통과율이 95% 이상이어야 함  
    checks: ['rate>0.95'],  
    // HTTP 요청 실패율이 5% 미만이어야 함  
    http_req_failed: ['rate<0.05'],  
}, };  
  
if (scenarioToRun === 'coupon_spike' || scenarioToRun === 'all') {  
    options.scenarios.coupon_spike = {  
        executor: 'ramping-arrival-rate', 
        startRate: 20,                    
        timeUnit: '1s',                   
        preAllocatedVUs: 50,                
        maxVUs: 50000,                    
        stages: [  
			{ duration: '15s', target: 500 },  
			{ duration: '5s', target: 1000 },  
			{ duration: '15s', target: 10000 },
        ],  
    };  
}  

```

* coupon_spike 시나리오
    * thresholds
        * checks - rate>0.95
            * 전체 체크 통과율 95% 이상
        * http_req_failed - rate<0.05
            * 전체 요청 실패율 5% 미만
    * executor: 'ramping-arrival-rate'
        *  전체 시스템의 초당 요청 수(도착률)를 직접 제어
    * startRate: 20
        *  테스트 시작 시 초당 20 req/s로 시작
    * timeUnit: '1s'
        *  요청 도착률은 1초 단위로 계산
    * preAllocatedVUs: 50
        *  요청을 처리하기 위해 미리 50명의 VU를 할당
    * maxVUs: 50000
        *  목표 요청률을 달성하기 위해 최대 50,000명의 VU까지 생성할 수 있다.
    * stages: 각 단계별로 전체 요청률을 변경
        *  15초 동안 20 → 500 req/s로 선형 증가
        *  5초 동안 500 → 1000 req/s로 선형 증가
        *  15초 동안 1000 → 10000 req/s로 선형 증가
#### 시나리오 테스트 결과

첫번째 테스트 A
```javascript
 
let options = { scenarios: {},thresholds: {  
    // 전체 체크 통과율이 95% 이상이어야 함  
    checks: ['rate>0.95'],  
    // HTTP 요청 실패율이 5% 미만이어야 함  
    http_req_failed: ['rate<0.05'],  
}, };  
  
if (scenarioToRun === 'coupon_spike' || scenarioToRun === 'all') {  
    options.scenarios.coupon_spike = {  
        executor: 'ramping-arrival-rate', 
        startRate: 20,                    
        timeUnit: '1s',                   
        preAllocatedVUs: 50,                
        maxVUs: 50000,                    
        stages: [  
			{ duration: '15s', target: 500 },  
			{ duration: '5s', target: 1000 },  
			{ duration: '15s', target: 10000 },
        ],  
    };  
}  

```

![](https://github.com/fkdl3919/hhplus-ecommerce/blob/main/docs/assets/Screenshot%202025-02-28%20at%2001.49.25.png)

- 분석결과
    - 초당 10000건의 피크 요청을 테스트 할 때 요청 처리율, 실패율, 응답시간 면에서 상당한 성능 문제가 보인다.
```bash
1. ✗ status is 200  
   ↳  61% — ✓ 18552 / ✗ 11452
--- 61% 의 요청만 200 응답 (18,552 성공, 11,452 실패)

2. ✗ response time is less than 500ms  
   ↳  60% — ✓ 18043 / ✗ 11961
--- 60%의 요청만 500ms 내에 응답됨 (18,043 통과, 11,961 실패)

1. ✗ checks.........................: 60.98% 36595 out of 60008
--- 전체 검사 통과율이 60.98%로 낮음 (총 60,008개 중 36,595개 통과)

1. dropped_iterations.............: 59760  918.253412/s
--- 59,760개 요청이 처리되지 못하고 초당 918.3개씩 폐기됨

5. http_req_blocked
--- 요청이 큐에서 대기한 시간

6. http_req_connecting
--- 연결 설정에 소요된 시간

7. http_req_duration
--- 요청 처리 총 소요시간

8. { expected_response:true }
--- 성공 응답의 소요시간

9. http_req_failed
--- 요청 실패율

10. http_req_receiving
--- 응답 수신 시간

11. http_req_sending
--- 요청 전송 시간

12. http_req_waiting
--- 서버 처리 대기 시간

1. http_reqs......................: 30004  461.032051/s
--- 초당 461.1개 요청 처리됨 (총 30,0045개)

14. iteration_duration
--- 테스트 반복 소요시간

15.
vus............................: 100 min=35 max=17594  
vus_max........................: 17603 min=50 max=17603
--- 가상 사용자: 최대 17,594명 동시 접속, 최대 설정 17,603명

```

- grafana 모니터링 자료

![](https://github.com/fkdl3919/hhplus-ecommerce/blob/main/docs/assets/Screenshot%202025-02-28%20at%2002.06.41.png)

![](https://github.com/fkdl3919/hhplus-ecommerce/blob/main/docs/assets/Screenshot%202025-02-28%20at%2002.06.55.png)

두번째 테스트 B
```javascript
 
let options = { scenarios: {},thresholds: {  
    // 전체 체크 통과율이 95% 이상이어야 함  
    checks: ['rate>0.95'],  
    // HTTP 요청 실패율이 5% 미만이어야 함  
    http_req_failed: ['rate<0.05'],  
}, };  
  
if (scenarioToRun === 'coupon_spike' || scenarioToRun === 'all') {  
    options.scenarios.coupon_spike = {  
		startRate: 20,         
		timeUnit: '1s',        
		preAllocatedVUs: 50,  
		maxVUs: 50000,         
		stages: [  
		{ duration: '15s', target: 100 },   
		{ duration: '5s', target: 1000 },  
    };  
}  

```

![](https://github.com/fkdl3919/hhplus-ecommerce/blob/main/docs/assets/Screenshot%202025-02-28%20at%2002.16.47.png)

### 테스트 A와 테스트B의 결과 보고서

- 테스트 A (단계: 15초 동안 target 500 → 5초 동안 target 1000 → 15초 동안 target 10000)  
  → 실패: 높은 부하(최대 초당 10,000 req/s) 시 서버의 응답률 및 체크 조건 미달
- 테스트 B (단계: 15초 동안 target 100 → 5초 동안 target 1000)  
  → 성공: 상대적으로 낮은 부하(최대 초당 1000 req/s)에서 성공

위 결과를 토대로, 서버가 더 많은 트래픽을 안정적으로 처리하기 위해 개선해야 할 주요 영역은 애플리케이션 서버(JVM 및 쓰레드 관리), 운영 서버 하드웨어, 데이터베이스 메모리 및 최적화이다.

#### 병목 현상 및 개선 필요 영역
##### 애플리케이션 서버 (JVM, 쓰레드)
- JVM 튜닝
    - Heap 사이즈 설정을 통하여 적절한 Heap 사이즈를 설정
    - `java -Xms2048m -Xmx2048m -jar application.jar`
- 쓰레드 관련
    - 쓰레드 풀 사이즈 변경: 동시에 처리할 수 있는 쓰레드 수를 늘려서, 높은 동시성 부하 상황에 요청처리를 적절히 처리하도록 변경
    - 비동기 처리: 선착순 쿠폰 발급 로직을 비동기 혹은 Message queue(kafka) 로 처리

##### 운영 서버 하드웨어 및 네트워크

- 서버 스펙
    - CPU: CPU 성능 향상 설정
- 운영 서버 증설 및 스케일 아웃 고려
    - 애플리케이션 서버를 클러스터로 구성하고, 로드 밸런서를 통한 트래픽 분산을 목적

##### 데이터베이스 (DB) 성능

- DB 메모리 및 캐싱
    - 데이터베이스 메모리 할당량을 늘리고, 캐시(redis)를 도입하여, 읽기/쓰기 부하를 분산.
- 인덱싱
    - 선착순 쿠폰 내의 유저, 쿠폰 조회 등 인덱스를 적용하여 쿼리 응답속도 최적화
