package kr.hhplus.be.server.interfaces.consumer.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrderRepository;
import kr.hhplus.be.server.domain.payment.event.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.command.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointConsumer {

    private final OutboxOrderRepository outboxOrderRepository;
    private final PointService pointService;

    @KafkaListener(topics = "${spring.kafka.topic.payment.success}", groupId = "point.consumer")
    public void consumeOutbox(PaymentSuccessEvent event) {
        pointService.use(PointCommand.Use.builder().userId(event.getUserId()).point(event.getPrice()).build());
    }

}
