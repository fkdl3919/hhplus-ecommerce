package kr.hhplus.be.server.infrastructure.jpa.outbox.payment;

import java.util.Optional;
import kr.hhplus.be.server.domain.payment.outbox.OutboxPayment;
import kr.hhplus.be.server.domain.payment.outbox.OutboxPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxPaymentRepositoryImpl implements OutboxPaymentRepository {

    private final OutboxPaymentJpaRepository outboxPaymentJpaRepository;

    @Override
    public void save(OutboxPayment outboxPayment) {
        outboxPaymentJpaRepository.save(outboxPayment);
    }

    @Override
    public Optional<OutboxPayment> findById(Long id) {
        return outboxPaymentJpaRepository.findById(id);
    }

}
