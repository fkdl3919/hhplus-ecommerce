import http from 'k6/http';
import { check, sleep } from 'k6';

// 환경 변수 SCENARIO를 사용하여 실행할 시나리오를 선택합니다.
// 기본값은 'all'로, 모든 시나리오를 실행합니다.
const scenarioToRun = __ENV.SCENARIO || 'all';

// options 객체를 선언합니다.
let options = { scenarios: {},
    thresholds: {
        // 전체 체크 통과율이 95% 이상이어야 함
        checks: ['rate>0.95'],
        // HTTP 요청 실패율이 5% 미만이어야 함
        http_req_failed: ['rate<0.05'],
    }, };

/*
 * coupon 시나리오
 * - executor: 'constant-vus'
 *   → 고정된 수의 가상 사용자(VU)를 사용합니다.
 * - vus: 1000
 *   → 1000명의 VU가 동시에 실행됩니다.
 * - duration: '2s'
 *   → 테스트는 2초 동안 진행됩니다.
 */
if (scenarioToRun === 'coupon' || scenarioToRun === 'all') {
    options.scenarios.coupon = {
        executor: 'constant-vus', // 고정된 VU 수로 부하 생성
        vus: 1000,                // 1000명의 VU를 사용
        duration: '2s',           // 2초간 실행
    };
}

/*
 * coupon_ramp_up 시나리오
 * - executor: 'ramping-vus'
 *   → VU 수를 시간에 따라 선형으로 증가 및 감소시키며 부하를 테스트합니다.
 * - startVus: 0
 *   → 테스트 시작 시 0명의 VU로 시작합니다.
 * - stages: 각 단계별로 VU 수를 변경합니다.
 *     1. 10초 동안 0 → 1000 VU로 증가
 *     2. 20초 동안 1000 → 2000 VU로 증가
 *     3. 20초 동안 2000 → 1000 VU로 감소
 *     4. 30초 동안 1000 → 0 VU로 감소
 */
if (scenarioToRun === 'coupon_ramp_up' || scenarioToRun === 'all') {
    options.scenarios.coupon_ramp_up = {
        executor: 'ramping-vus',  // VU 수를 시간에 따라 조절하는 실행기
        startVus: 0,              // 시작 시 VU 수 0명
        stages: [
            { duration: '10s', target: 1000 }, // 10초 동안 0명에서 1000명으로 선형 증가
            { duration: '20s', target: 2000 }, // 20초 동안 1000명에서 2000명으로 선형 증가
            { duration: '20s', target: 1000 }, // 20초 동안 2000명에서 1000명으로 선형 감소
            { duration: '30s', target: 0 },    // 30초 동안 1000명에서 0명으로 선형 감소
        ],
    };
}

/*
 * coupon_spike 시나리오
 * - executor: 'ramping-arrival-rate'
 *   → 전체 시스템의 초당 요청 수(도착률)를 직접 제어합니다.
 * - startRate: 20
 *   → 테스트 시작 시 초당 20 req/s로 시작합니다.
 * - timeUnit: '1s'
 *   → 요청 도착률은 1초 단위로 계산합니다.
 * - preAllocatedVUs: 50
 *   → 요청을 처리하기 위해 미리 50명의 VU를 할당합니다.
 * - maxVUs: 10000
 *   → 목표 요청률을 달성하기 위해 최대 10,000명의 VU까지 생성할 수 있습니다.
 * - stages: 각 단계별로 전체 요청률을 변경합니다.
 *     1. 10초 동안 20 → 500 req/s로 선형 증가
 *     2. 5초 동안 500 → 10000 req/s로 선형 증가
 *     3. 10초 동안 10000 → 500 req/s로 선형 감소 (스파이크 후 회복)
 */
if (scenarioToRun === 'coupon_spike' || scenarioToRun === 'all') {
    options.scenarios.coupon_spike = {
        executor: 'ramping-arrival-rate', // 요청 도착률(초당 요청 수)을 제어하는 실행기
        startRate: 20,                    // 시작 시 초당 20 req/s로 시작
        timeUnit: '1s',                   // 1초 단위로 요청률을 계산
        preAllocatedVUs: 50,              // 최소 50명의 VU를 미리 할당
        maxVUs: 10000,                    // 최대 10,000명의 VU를 생성할 수 있음
        stages: [
            { duration: '10s', target: 500 },   // 10초 동안 초당 요청 수를 20에서 500까지 선형 증가
            { duration: '5s', target: 10000 },    // 5초 동안 초당 요청 수를 500에서 10000으로 선형 증가
            { duration: '10s', target: 500 },     // 10초 동안 초당 요청 수를 10000에서 500으로 선형 감소 (회복)
        ],
    };
}

if (scenarioToRun === 'coupon_spike2' || scenarioToRun === 'all') {
    options.scenarios.coupon_spike = {
        executor: 'ramping-arrival-rate',
            startRate: 20,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 50000,
            stages: [
            { duration: '15s', target: 100 },
            { duration: '5s', target: 1000 },
        ],
    }
}

export { options };

export default function () {
    const userId = __VU

    let res = http.post(
        `http://localhost:8080/api/v1/coupon/request/1?userId=${userId}`);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time is less than 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}
