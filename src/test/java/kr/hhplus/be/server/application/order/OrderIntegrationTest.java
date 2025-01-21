package kr.hhplus.be.server.application.order;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.Order;
import kr.hhplus.be.server.domain.order.command.OrderCommand.Order.OrderItemCommand;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.info.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ProductService productService;


    public User setUpUser() {
        return userRepository.save(User.builder().build());
    }

    public Point setUpPoint(long userId, long userPoint) {
        return pointRepository.save(Point.builder().userId(userId).point(userPoint).build());
    }


    public Product setUpProduct(int stock, long price) {
        return productRepository.save(Product.builder().price(price).stock(stock).build());
    }

    public List<Product> setUpProducts(int stock, long price, long loop) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            products.add(setUpProduct(stock, price));
        }
        return products;
    }

    public Coupon setUpCoupon(int discountRate) {
        return couponRepository.save(Coupon.builder().stock(10).discountRate(discountRate).build());
    }

    public IssuedCoupon setUpIssuedCoupon(Coupon coupon, User user) {
        return couponService.issueCoupon(coupon.getId(), user.getId());
    }

    /**
     * @return 주문금액
     */
    public Long getOrderPrice(Long price, int quantity){
        return price * quantity;
    }

    public List<User> setUpUsers(int loop) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            User user = setUpUser();
            setUpPoint(user.getId(), 10000);
            users.add(user);
        }
        return users;
    }


    /**
     * 주문 및 결제 통합테스트
     */
    @Test
    @DisplayName("case - 주문 및 결제 성공 통합테스트")
    public void orderPaymentTest(){
        // given
        User user = setUpUser();

        // 유저 보유포인트
        final long userPoint = 10000;

        setUpPoint(user.getId(), userPoint);


        // 상품 가격
        final Long productPrice = 1000L;
        final int productStock = 10;

        Product product = setUpProduct(productStock, productPrice);

        // 상품 별 주문 수량
        final int quantity = 10;
        List<OrderItemCommand> orderItemCommands = List.of(
            new OrderItemCommand(product.getId(), quantity)
        );

        // 주문가격
        Long orderPrice = orderItemCommands.stream().mapToLong((command) -> getOrderPrice(productPrice, command.quantity())).sum();

        OrderCommand.Order command = OrderCommand.Order
            .builder()
            .userId(user.getId())
            .issuedCouponId(null)
            .products(orderItemCommands)
            .orderPrice(orderPrice)
            .build();


        // when
        OrderInfo order = orderFacade.orderPayment(command);

        // then
        assertEquals(OrderStatus.CONFIRMED, order.status());

    }

    @Test
    @DisplayName("case - 주문결제 시 잔액이 부족할 경우 IllegalArgumentException 발생")
    public void orderPaymentTest2(){
        // given
        User user = setUpUser();

        // 유저 보유포인트
        final long userPoint = 1000;

        setUpPoint(user.getId(), userPoint);

        // 상품 가격
        final Long productPrice = 10000L;
        final int productStock = 10;
        Product product = setUpProduct(productStock, productPrice);

        // 상품 별 주문 수량
        final int quantity = 10;
        List<OrderItemCommand> orderItemCommands = List.of(
            new OrderItemCommand(product.getId(), quantity)
        );

        // 주문가격
        Long orderPrice = orderItemCommands.stream().mapToLong((command) -> getOrderPrice(productPrice, command.quantity())).sum();


        OrderCommand.Order command = OrderCommand.Order
            .builder()
            .userId(user.getId())
            .issuedCouponId(null)
            .products(orderItemCommands)
            .orderPrice(orderPrice)
            .build();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderFacade.orderPayment(command));

        // then
        assertEquals("잔액이 부족합니다", exception.getMessage());
    }

    @Test
    @DisplayName("case - 주문결제 시 여러 상품을 주문했을 때 OrderItem이 올바르게 입력되었는지 확인")
    public void orderPaymentTest3(){
        // given
        User user = setUpUser();

        // 유저 보유포인트 100000
        final long userPoint = 100000;
        setUpPoint(user.getId(), userPoint);

        // 쿠폰
        int discountRate = 10;
        Coupon coupon = setUpCoupon(discountRate);
        IssuedCoupon issuedCoupon = setUpIssuedCoupon(coupon, user);


        // setup 상품 가격 100
        final Long productPrice = 100L;

        // setup 상품 수량
        final int productStock = 10;

        // setup 상품 수
        int loop = 10;
        List<Product> products = setUpProducts(productStock, productPrice, loop);

        // 상품 별 주문 수량
        final int quantity = 10;

        // 주문 아이템
        List<OrderItemCommand> orderItemCommands = new ArrayList<>();

        // 주문의 상품들
        int orderItemLoop = 5;
        for (int i = 0; i < orderItemLoop; i++) {
            orderItemCommands.add(new OrderItemCommand(products.get(i).getId(), quantity));
        }

        // 주문가격
        Long orderPrice = orderItemCommands.stream().mapToLong((command) -> getOrderPrice(productPrice, command.quantity())).sum();

        // 주문 커맨드 생성
        OrderCommand.Order command = OrderCommand.Order
            .builder()
            .userId(user.getId())
            .issuedCouponId(issuedCoupon.getId())
            .products(orderItemCommands)
            .orderPrice(orderPrice)
            .build();

        // when
        OrderInfo order = orderFacade.orderPayment(command);
        List<OrderItem> orderItems = orderRepository.findOrderItemListByOrderId(order.id());

        // then
        assertEquals(OrderStatus.CONFIRMED, order.status());
        assertEquals(orderItemLoop, orderItems.size());

    }

    @Test
    @DisplayName("case - 주문결제 성공 시 쿠폰 할인율이 적용되었는지 확인")
    public void orderPaymentTest4(){
        // given
        User user = setUpUser();

        // 유저 보유포인트
        final long userPoint = 1000;
        setUpPoint(user.getId(), userPoint);

        // 쿠폰
        int discountRate = 10;
        Coupon coupon = setUpCoupon(discountRate);
        IssuedCoupon issuedCoupon = setUpIssuedCoupon(coupon, user);


        // 상품 가격
        final Long productPrice = 100L;
        final int productStock = 10;
        Product product = setUpProduct(productStock, productPrice);

        // 상품 별 주문 수량
        final int orderQuantity = 10;

        List<OrderItemCommand> orderItemCommands = List.of(
            new OrderItemCommand(product.getId(), orderQuantity)
        );

        // 주문가격
        Long orderPrice = orderItemCommands.stream().mapToLong((command) -> getOrderPrice(productPrice, command.quantity())).sum();


        OrderCommand.Order command = OrderCommand.Order
            .builder()
            .userId(user.getId())
            .issuedCouponId(issuedCoupon.getId())
            .products(orderItemCommands)
            .orderPrice(orderPrice)
            .build();


        // when
        OrderInfo order = orderFacade.orderPayment(command);
        Payment payment = paymentRepository.findPayMentByOrderId(order.id());

        // then
        assertEquals(OrderStatus.CONFIRMED, order.status());
        // 쿠폰적용 전 결제금액
        assertEquals(order.orderPrice(), orderPrice);

        // 쿠폰적용 후 결제금액
        long payPrice = orderPrice - (orderPrice * discountRate / 100);
        assertEquals(payment.getPayPrice(), payPrice);

    }


    @Test
    @DisplayName("case - 주문결제 시 성공 시 외부플랫폼으로 데이터가 전송되었는지 확인")
    public void orderPaymentTest5(){
        // given
        User user = setUpUser();

        // 유저 보유포인트
        final long userPoint = 1000;

        setUpPoint(user.getId(), userPoint);

        // 상품 가격
        final Long productPrice = 10000L;
        final int productStock = 10;
        Product product = setUpProduct(productStock, productPrice);

        // 상품 별 주문 수량
        final int quantity = 10;
        List<OrderItemCommand> orderItemCommands = List.of(
            new OrderItemCommand(product.getId(), quantity)
        );

        // 주문가격
        Long orderPrice = orderItemCommands.stream().mapToLong((command) -> getOrderPrice(productPrice, command.quantity())).sum();

        OrderCommand.Order command = OrderCommand.Order
            .builder()
            .userId(user.getId())
            .issuedCouponId(null)
            .products(orderItemCommands)
            .orderPrice(orderPrice)
            .build();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderFacade.orderPayment(command));

        // then
        assertEquals("잔액이 부족합니다", exception.getMessage());
    }

    /**
     * 여러명의 유저가 하나의 상품에 n개 주문
     */
    @Test
    @DisplayName("동시성 테스트 - 여러명의 유저가 같은 상품을 주문할 경우 주문 완료 후 상품 재고가 정확한지 테스트")
    public void orderPaymentConcurrencyTest() throws InterruptedException {
        // given
        List<User> users = setUpUsers(1);


        final Long productPrice = 100L;
        final int productStock = 1000;
        Product product = setUpProduct(productStock, productPrice);

        final int quantity = 10;
        List<OrderItemCommand> orderItemCommands = List.of(
            new OrderItemCommand(product.getId(), quantity)
        );

        List<OrderCommand.Order> orders = new ArrayList<>();

        long orderPrice = orderItemCommands.stream().mapToLong((command) -> getOrderPrice(productPrice, command.quantity())).sum();

        for (User user : users) {
            orders.add(
                OrderCommand.Order
                    .builder()
                    .userId(user.getId())
                    .issuedCouponId(null)
                    .products(orderItemCommands)
                    .orderPrice(orderPrice)
                    .build()
            );
        }

        int threadCount = orders.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);


        // when
        for (OrderCommand.Order order : orders) {
            executorService.submit(() -> {
                try {
                    orderFacade.orderPayment(order);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        // then
        Product byId = productRepository.findById(product.getId()).orElse(null);
        assertEquals( productStock - (users.size() * quantity), byId.getStock());


    }


}
