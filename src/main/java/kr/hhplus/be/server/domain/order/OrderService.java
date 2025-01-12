package kr.hhplus.be.server.domain.order;

import java.util.List;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;


    // 주문 생성
    @Transactional
    public Order order(OrderCommand orderCommand) {
        Order order = Order.builder()
            .user(User.builder().id(orderCommand.userId()).build())
            .status(OrderStatus.PENDING)
            .issuedCoupon(IssuedCoupon.builder().id(orderCommand.issuedCouponId()).build())
            .build();

        orderRepository.save(order);

        List<OrderItemCommand> products = orderCommand.products();

        // 주문 item별로 생성
        products.stream().forEach((orderItem) -> {
            OrderItem build = OrderItem.builder()
                .order(order)
                .product(Product.builder().id(orderItem.productId()).build())
                .quantity(orderItem.quantity())
                .build();
            orderRepository.saveOrderItem(build);
        });

        return order;
    }
}
