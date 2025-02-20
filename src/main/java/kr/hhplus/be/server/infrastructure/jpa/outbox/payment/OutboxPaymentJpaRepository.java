package kr.hhplus.be.server.infrastructure.jpa.outbox.payment;

import kr.hhplus.be.server.domain.order.outbox.OutboxOrder;
import kr.hhplus.be.server.domain.payment.outbox.OutboxPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxPaymentJpaRepository extends JpaRepository<OutboxPayment, Long> {

}
