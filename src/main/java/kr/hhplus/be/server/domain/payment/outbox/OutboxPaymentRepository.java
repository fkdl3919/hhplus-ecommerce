package kr.hhplus.be.server.domain.payment.outbox;

import java.util.Optional;

public interface OutboxPaymentRepository {

    void save(OutboxPayment outboxPayment);

    Optional<OutboxPayment> findById(Long id);
}
