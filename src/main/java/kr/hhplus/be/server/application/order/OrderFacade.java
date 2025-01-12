package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.dataplatform.Dataplatform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final ProductService productService;
    private final CouponService couponService;

    @Transactional
    public void order(OrderCommand command) {
        // 사용자 검증 및 포인트 조회
        User user = userService.findUser(command.userId());
        Point userPoint = userService.findPoint(command.userId());

        // 상품 검증
        Product product = productService.findProductWithLock(command.productId());
        product.validForOrder(command.stock(), userPoint.getPoint());

        // 쿠폰 검증
        long discountRate = 0;
        IssuedCoupon issuedCoupon = null;
        if(command.issuedCouponId() != 0) {
            issuedCoupon = couponService.findIssuedCouponById(command.issuedCouponId());
            issuedCoupon.validForOrder();

            Coupon coupon = issuedCoupon.getCoupon();

            discountRate = coupon.getDiscountRate();
        }

        Long originalPrice = product.getPrice() * command.stock() ;
        Long orderPrice = originalPrice - (originalPrice * discountRate / 100) ;

        // 주문 생성
        Order order = orderService.order(
            Order.builder()
                .originalPrice(originalPrice)
                .orderPrice(orderPrice)
                .user(user)
                .status(OrderStatus.PENDING)
                .issuedCoupon(issuedCoupon)
                .build()
        );

        // 결제 및 포인트 차감
        Payment payment = paymentService.pay(
            Payment.builder()
                .user(user)
                .order(order)
                .amount(orderPrice)
                .status(PaymentStatus.PENDING)
                .build(),
            userPoint
        );

        // 결제가 완료되지 않았다면 exception
        if(payment.getStatus() != PaymentStatus.CONFIRMED) throw new RuntimeException("결제가 실패하였습니다.");

        // 결제 완료 시 주문 완료
        order.setStatus(OrderStatus.CONFIRMED);

        // 외부 플랫폼으로 데이터 전송
        Dataplatform.sendData(order);

    }
}
