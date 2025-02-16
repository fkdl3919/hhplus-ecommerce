## 인덱스

### 기존 시나리오에서 사용하는 조회 API

1. 포인트 조회
   - ```sql
        select * from point where user_id = ?;
        ```
2. 상품 조회
   - ```sql
        select * from product where id = ?;
        ```
3. 상품 목록조회
   - ```sql
         select * from product order by created_at desc limit ? offset ?;
        ```
4. 인기 상품 목록조회
   - ```sql
         select * from product p 
            join orderItem oi on oi.product_id = p.id
            join order o on o.id = oi.order_id and o.status = 'CONFIRM'
            where o.ordered_at > ?
            order by oi.quantity desc limit 5;
        ```

### 인덱스를 고려해볼 때 적용할 수 있는 조건들

1. 카디널리티가 높은 컬럼
   - 인덱스는 보통 B-TREE 자료구조로 구현되어 정렬된 상태에서 값을 찾기 때문에 카디널리티가 높은 컬럼에 인덱스를 적용하는 것이 유리하다.
   - 여러 컬럼으로 이루어진 인덱에서는 일반적으로 카디널리티가 높은 컬럼을 앞에 두는 것이 좋다.
   - (.e.g. 주문(유저 ID, 주문 상태), 주문상품(주문 ID, 상품 ID))
```text
허재 코치님 의견
ordered_at <- 통계 같은걸 위해서 자주 조회될 것... 하면 됨. 오히려 좋아.
     왜 오히려 좋아? 시간순 정렬...
     인서트 되는 순서랑 거의 똑같음.. 그래서 인덱스 조정 비용이 적음.
```
2. 등치조건(=)
   - 등치 조건은 인덱스의 왼쪽부터 순차적으로 매칭되며 효율적으로 필터링할 수 있다.
3. 범위 조건(<, >, BETWEEN, LIKE 등)
   - 범위 조건도 인덱스 사용에 도움이 되나, 인덱스의 순서에 따라 이후 컬럼의 활용도가 제한될 수 있음.(범위 조건 인덱스 사용 후 그 뒤 컬럼은 적용 X)
3. 인덱스 재정렬 최소화 - 데이터 삽입, 수정이 적은 컬럼
   - insert, update, delete같은 데이터 변경 쿼리가 잦은 경우 paging이 빈번해져 성능이 악화될 수 있다.
4. Nullable 컬럼은 가급적 지양
   - null이 허용되나 null이 색인에 있어 비약적인 성능 저하를 가져오므로, 일반적으로 Nullable한 데이터의 경우 Indexing을 하지 않는다.
5. 인덱스 컬럼의 데이터 타입
   - 인덱스 컬럼의 데이터 타입이 큰 경우 인덱스의 크기가 커져 성능이 저하될 수 있다.
6. '주문완료', '주문취소' / 1, 0 등 값의 종류가 제한적인 경우
   - 단독인덱스로 사용시 값의 분포도가 넓어 인덱스의 효율이 떨어질 수 있으나, 여러 컬럼으로 이루어진 인덱스로 사용시 효율적으로 사용할 수 있다.


### 시나리오를 기준으로 적용해볼 만한 인덱스 컬럼들

1. 포인트 조회
   - point(user_id) 포인트와 유저는 1:1 관계를 갖고있으며 카디널리티가 높은 컬럼이므로 인덱스를 적용할 수 있다.

[//]: # (2. 주문 목록 조회)

[//]: # (   - order&#40;user_id, status&#41;: 두개의 칼럼을 포함한 쿼리 사용성을 고려하여 인덱스를 적용)
2. 인기 상품 조회
   - order(status, orderd_at): 주문 상태로 정렬 및 주문일자로 범위조건을 사용하므로 인덱스를 적용
   - orderItem(order_id, product_id, quantity): 주문상품의 주문 ID와 상품 ID은 unique한 값이므로 인덱스를 적용, quantity는 정렬에 사용되므로 인덱스에 포함

[//]: # (   - orderItem&#40;state, quantity desc&#41;: order by를 위해 상품의 판매량을 기준으로 정렬하므로 인덱스를 적용)


### 인덱스 성능 테스트

1. 포인트 조회 테스트
   - 사용 인덱스: point(user_id)
   - 테스트 데이터 수: 30만건
   - 테스트 쿼리
   - ```sql
       select * from point where user_id = ?
      ```
   - 기대효과: user_id로 조회 시 인덱스를 통한 응답속도 향상
   - 결과
      - 인덱스 미사용 시: 112 milliseconds
      - 인덱스 사용 시: 60 milliseconds

2. 인기 상품 조회 테스트
   - 사용 인덱스: Order(idx_status_ordered_at), OrderItem(idx_order_id_product_id_quantity)
   - 테스트 데이터 수: Order 30만건, OrderItem 30만건
   - 테스트 쿼리
   - ```sql
       SELECT p.*
            FROM product p
            JOIN order_item oi ON oi.product_id = p.id
            JOIN order_t o ON oi.order_id = o.id  AND o.status = 'CONFIRMED'
        WHERE o.ordered_at > NOW() - INTERVAL 3 DAY
        GROUP BY p.id
        ORDER BY sum(oi.quantity) DESC
        LIMIT 5
      ```
     - 기대효과: idx_order_id_product_id_quantity 와 idx_status_ordered_at 인덱스를 통한 응답속도 향상
     - 결과
        - 인덱스 미사용 시: 6000 milliseconds
        - 인덱스 사용 시: 58500 milliseconds
        - 인덱스 사용 후 오히려 성능이 저하되는 현상이 있었다.
        - 이유: order의 status 분포도가 넓어 idx_status_ordered_at 사용 시 range type으로 사용이 되어 성능이 저하되었다.
   - 개선방안: idx_status_ordered_at 를 삭제하고 OrderItem(product_id, quantity) 만 사용하여 인덱스를 적용하였다.
     - 결과
       - 인덱스 미 사용시: 6000 milliseconds
       - 인덱스 사용 시: 1931 milliseconds
