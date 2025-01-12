package kr.hhplus.be.server.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.info.OrderInfo;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

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


    public User setUpUser() {
        return userRepository.save(User.builder().build());
    }

    public Point setUpPoint(long userId, long userPoint) {
        return pointRepository.save(Point.builder().user(User.builder().id(userId).build()).point(userPoint).build());
    }


    public Product setUpProduct(int stock, long price) {
        return productRepository.save(Product.builder().price(price).stock(stock).build());
    }

    public Coupon setUpCoupon() {
        return couponRepository.save(Coupon.builder().stock(10).discountRate(10).build());
    }

    public IssuedCoupon setUpIssuedCoupon(Coupon coupon, User user) {
        return couponService.issueCoupon(coupon.getId(), user.getId());
    }


    /**
     * 주문 및 결제 통합테스트
     */
    @Test
    @DisplayName("case - 주문 및 결제 통합테스트")
    public void orderPaymentTest(){
        // given
        User user = setUpUser();

        // 유저 보유포인트 1000
        long userPoint = 10000;

        setUpPoint(user.getId(), userPoint);

        int productStock = 10;

        // 상품 가격 1000
        int productPrice = 1000;
        Product product = setUpProduct(productStock, productPrice);
        Coupon coupon = setUpCoupon();
        IssuedCoupon issuedCoupon = setUpIssuedCoupon(coupon, user);

        // 상품 별 주문 수량
        long quantity = 10;
        List<OrderItemCommand> orderItemCommands = List.of(
            new OrderItemCommand(product.getId(), quantity)
        );

        OrderCommand command = new OrderCommand(
            user.getId(),
            issuedCoupon.getId(),
            orderItemCommands
        );

        // when
        OrderInfo order = orderFacade.orderPayment(command);

        // then
        assertEquals(OrderStatus.CONFIRMED, order.status());

    }

}
