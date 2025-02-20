package kr.hhplus.be.server.interfaces.event.order;

import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrder;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrderRepository;
import kr.hhplus.be.server.infrastructure.kafka.order.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderListener {

    private final OutboxOrderRepository outboxOrderRepository;
    private final OrderProducer orderProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onOrderRequest(OrderEvent.OrderRequest event) {
        outboxOrderRepository.save(OutboxOrder.builder().orderId(event.orderId()).status(OutboxOrderStatus.PENDING).build());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void success(OrderEvent.OrderRequest event) {
        orderProducer.request(event);
    }

}
