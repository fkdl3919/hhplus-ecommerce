package kr.hhplus.be.server.interfaces.event.payment;

import kr.hhplus.be.server.domain.payment.enums.OutboxPaymentStatus;
import kr.hhplus.be.server.domain.payment.event.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.outbox.OutboxPayment;
import kr.hhplus.be.server.domain.payment.outbox.OutboxPaymentRepository;
import kr.hhplus.be.server.infrastructure.kafka.payment.PaymentProducer;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class PaymentListener {

    private final OutboxPaymentRepository outboxPaymentRepository;
    private final PaymentProducer paymentProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPaymentRequest(PaymentSuccessEvent event) {
        outboxPaymentRepository.save(OutboxPayment.builder().paymentId(event.getPaymentId()).status(OutboxPaymentStatus.PENDING).build());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void success(PaymentSuccessEvent event) {
        paymentProducer.success(event);
    }

}
