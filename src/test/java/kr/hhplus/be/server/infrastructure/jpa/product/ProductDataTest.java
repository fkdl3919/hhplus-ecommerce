package kr.hhplus.be.server.infrastructure.jpa.product;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.jpa.order.OrderItemJpaRepository;
import kr.hhplus.be.server.infrastructure.jpa.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.jpa.product.ProductJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@Slf4j
@SpringBootTest
public class ProductDataTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;


    void setUp(int count) {
        productJpaRepository.deleteAll();

        for (int i = 0; i < count; i++) {
            productJpaRepository.save(
                Product.builder()
                    .name("product" + (i + 1))
                    .price(1000L * (i + 1))
                    .stock(10)
                    .build()
            );
        }
    }

    public User setUpUser() {
        return userRepository.save(User.builder().build());
    }

    public void setUpProduct(int stock, long price, int count) {
        final int batchSize = 10000; // 배치 사이즈 설정

        for (int i = 0; i < count; i++) {
            Product product = Product.builder()
                .price(price)
                .name("상품" + (i + 1))
                .stock(stock)
                .build();
            productJpaRepository.save(product);

            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

    }

    public void setUpOrder(long userId, int count) {
        final int batchSize = 10000; // 배치 사이즈 설정

        for (int i = 0; i < count; i++) {
            int orderQauntity = i + 1; // 주문수량 랜덤설정

            OrderStatus confirmed = OrderStatus.CONFIRMED;
            if (i % 2 == 0) {
                confirmed = OrderStatus.CANCELLED;
            }

            Order order = Order.builder()
                .userId(userId)
                .status(confirmed)
                .orderedAt(LocalDateTime.now())
                .build();


            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productId((long) i + 1)
                .quantity(orderQauntity).build();

            orderRepository.save(order);

            orderRepository.saveOrderItem(orderItem);

            if (i % (batchSize/2) == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }


    /**
     * 상품 목록 조회 테스트
     * - 페이징 처리가 올바른지 테스트
     */
    @Test
    @DisplayName("case - 상품 목록 조회 시 페이징 처리가 올바른지 테스트")
    public void productsTest1(){
        // given
        int setUpCount = 25;
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) setUpCount / pageSize);

        // setUpCount 수 만큼 insert
        setUp(setUpCount);

        // when
        for (int page = 0; page < totalPages; page++) {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, setUpCount);

            // 각 페이지 별 아이템 수 기대값
            int itemsPerPage = end - start;

            // page 별 pageable 생성
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Product> products = productRepository.selectProductPaging(pageable);

            // then
            // 조회 시 페이지별 수량 비교
            assertEquals(itemsPerPage, products.getNumberOfElements());
        }

    }


    /**
     * 인기상품 조회
     * 인덱스 사용 테스트
     * Order(idx_status_ordered_at), OrderItem(idx_order_id_product_id_quantity)
     */
    @Test
    @Transactional
    @DisplayName("case - 최근 3일간 가장 많이 팔린 상위 5개 상품정보를 조회 인덱스 사용 테스트")
    public void topSellerTest1(){
        // given
        User user = setUpUser();

        // setup 상품 가격 100
        final long productPrice = 100;

        // setup 상품 수량
        final int productStock = 1000;

        // setup 상품
        int count = 10;
        setUpProduct(productStock, productPrice, count);

        // setup 주문
        setUpOrder(user.getId(), count);

        String sql = """
            explain
            SELECT p.*
            FROM product p
            JOIN order_item oi ON oi.product_id = p.id
            JOIN order_t o ON oi.order_id = o.id  AND o.status = 'CONFIRMED'
            WHERE o.ordered_at > NOW() - INTERVAL 3 DAY
            ORDER BY oi.quantity DESC
            LIMIT 5
        """;
        Query query = entityManager.createNativeQuery(sql);

        // when
        // 실행계획 출력
        long startTime = System.currentTimeMillis();
        List<Object[]> results = query.getResultList();
        long endTime = System.currentTimeMillis();

        log.info("실행시간 : {} milliseconds", endTime - startTime);

        results.forEach(result -> {
            log.info(Arrays.toString(result));
        });

        // then
        boolean indexUsed = results.stream()
            .anyMatch(result -> Arrays.stream(result).anyMatch( r -> (r + "").contains("idx_status_ordered_at")));

        boolean indexUsed2 = results.stream()
            .anyMatch(result -> Arrays.stream(result).anyMatch( r -> (r + "").contains("idx_order_id_product_id_quantity")));


        assertTrue(indexUsed);
        assertTrue(indexUsed2);
    }

    /**
     * 14795 milliseconds
     */
    @Test
    @DisplayName("case - 최근 3일간 가장 많이 팔린 상위 5개 상품정보를 조회 인덱스 성능 테스트")
    public void topSellerTest2(){



        // given
        User user = setUpUser();

        // setup 상품 가격 100
        final long productPrice = 100;

        // setup 상품 수량
        final int productStock = 1000;

        // setup 상품
        int count = 600000;


        TransactionTemplate aopForTransaction = new TransactionTemplate(transactionManager);

        aopForTransaction.execute((status) -> {
            try {
                setUpProduct(productStock, productPrice, count);

                // setup 주문
                setUpOrder(user.getId(), count);
            } catch (Throwable throwable) {
                status.setRollbackOnly();
                log.error("DistributeLockAop Error", throwable);
            }
            return null;
        });



        // when
        // 실행계획 출력
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            productRepository.selectTopSellingProducts();
        }
        long endTime = System.currentTimeMillis();

        log.info("실행시간 : {} milliseconds", endTime - startTime);

    }

    /**
     * 14795 milliseconds
     */
    @Test
    @Transactional
    @DisplayName("case - 최근 3일간 가장 많이 팔린 상위 5개 상품정보를 조회 인덱스 미사용시 성능 테스트")
    public void topSellerTest3(){
//        String dropIndex = "drop index idx_status_ordered_at on order_t";
//        String dropIndex2 = "drop index idx_order_id_product_id_quantity on order_item";
//
//        entityManager.createNativeQuery(dropIndex).executeUpdate();
//        entityManager.createNativeQuery(dropIndex2).executeUpdate();

        // given
        User user = setUpUser();

        // setup 상품 가격 100
        final long productPrice = 100;

        // setup 상품 수량
        final int productStock = 1000;

        // setup 상품
        int count = 600000;
        setUpProduct(productStock, productPrice, count);

        // setup 주문
        setUpOrder(user.getId(), count);

        // when
        // 실행계획 출력
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            productRepository.selectTopSellingProducts();
        }
        long endTime = System.currentTimeMillis();

        log.info("실행시간 : {} milliseconds", endTime - startTime);

    }

}
