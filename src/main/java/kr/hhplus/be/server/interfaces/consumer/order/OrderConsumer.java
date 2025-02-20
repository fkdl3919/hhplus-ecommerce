package kr.hhplus.be.server.interfaces.consumer.order;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrderRepository;
import kr.hhplus.be.server.domain.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final OutboxOrderRepository outboxOrderRepository;
    private final OrderService orderService;

    @KafkaListener(topics = "${spring.kafka.topic.order.request}", groupId = "outbox.order")
    public void consumeOutbox(OrderEvent.OrderRequest event) {
        outboxOrderRepository.findById(event.orderId()).orElseThrow(EntityNotFoundException::new).setStatus(OutboxOrderStatus.CONFIRMED);
    }

    @KafkaListener(topics = "${spring.kafka.topic.payment.success}", groupId = "order.consumer")
    public void consumeOutbox(PaymentSuccessEvent event) {
        orderService.confirmOrder(event.getOrderId());
    }

}
