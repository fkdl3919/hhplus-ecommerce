package kr.hhplus.be.server.domain.order;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.Order.OrderItemCommand;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent.OrderRequest;
import kr.hhplus.be.server.infrastructure.kafka.order.OrderProducer;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    // 주문 생성
    @Transactional
    public Order order(OrderCommand.Order command) {

        Order order = Order.builder()
            .userId(command.userId())
            .issuedCouponId(command.issuedCouponId())
            .orderPrice(command.orderPrice())
            .status(OrderStatus.PENDING)
            .build();

        Order save = orderRepository.save(order);

        if(save.getId() == null){
            throw new EntityNotFoundException("주문이 생성되지 않았습니다.");
        }

        List<OrderItemCommand> products = command.products();

        // 주문 item별로 생성
        products.stream().forEach((orderItem) -> {
            OrderItem build = OrderItem.builder()
                .order(order)
                .productId(orderItem.productId())
                .quantity(orderItem.quantity())
                .build();
            orderRepository.saveOrderItem(build);
        });

        // 주문 이벤트 발생
        OrderEvent.OrderRequest orderRequest = OrderRequest.builder()
            .orderId(order.getId())
            .userId(order.getUserId())
            .command(command)
            .build();

        applicationEventPublisher.publishEvent(orderRequest);

        return order;
    }

    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

}
