package kr.hhplus.be.server.application.order;

import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.info.OrderInfo;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.command.PaymentCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.command.PointCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.command.ProductCommand;
import kr.hhplus.be.server.infrastructure.kafka.order.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;

    @Transactional
    public OrderInfo orderPayment(OrderCommand.Order command) {
        Long amountOfProducts = productService.getAmountOfProducts(
            command.products().stream().map((orderItemCommand) -> ProductCommand.GetAmount.builder().productId(orderItemCommand.productId()).quantity(orderItemCommand.quantity()).build()).collect(Collectors.toList())
        );
        command.validateAmount(amountOfProducts);
        Order order = orderService.order(command);
        return OrderInfo.of(order);
    }

}
