package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.info.OrderInfo;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.command.PaymentCommand;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.PointHistory;
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
    public OrderInfo orderPayment(OrderCommand command) {
        // 사용자 및 포인트 조회
        User user = userService.findUser(command.userId());
        Point userPoint = userService.findPoint(command.userId());

        /**
         * 주문
         */
        // 상품 및 수량확인
        AmountCalculator amountCalculator = new AmountCalculator();

        for (OrderItemCommand o : command.products()) {
            Product product = productService.findProductWithLock(o.productId());
            product.verifyProductStock(o.quantity(), userPoint.getPoint());

            // 상품 주문금액 계산 후 누적
            amountCalculator.addAmount(product.getPrice(), o.quantity());
        }

        // 주문생성 (상품 id, 상품 수량)
        Order order = orderService.order(command);

        /**
         * 결제
         */
        // 쿠폰 할인율
        long discountRate = couponService.getDiscountRate(command.issuedCouponId());

        Payment payment = paymentService.pay(
            new PaymentCommand(user.getId(), order.getId(), amountCalculator.getTotalAmount(), amountCalculator.getDiscountAmount(discountRate))
        );

        /**
         * 포인트 사용 및 히스토리 저장, 주문 성공
         */
        if(payment.getId() != null && payment.getStatus() == PaymentStatus.CONFIRMED){
            PointHistory pointHistory = userPoint.use(amountCalculator.getDiscountAmount(discountRate));
            userService.use(pointHistory);

            // 주문 상태 성공
            order.confirmOrder();

            // 외부 플랫폼으로 데이터 전송
            Dataplatform.sendData(order);
        }

        return OrderInfo.of(order);

    }

}
