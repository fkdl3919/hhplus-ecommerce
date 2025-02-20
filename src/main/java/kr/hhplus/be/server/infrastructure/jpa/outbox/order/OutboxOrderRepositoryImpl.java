package kr.hhplus.be.server.infrastructure.jpa.outbox.order;

import java.util.Optional;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrder;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxOrderRepositoryImpl implements OutboxOrderRepository {

    private final OutboxOrderJpaRepository outboxOrderJpaRepository;

    @Override
    public void save(OutboxOrder outboxOrder) {
        outboxOrderJpaRepository.save(outboxOrder);
    }

    @Override
    public Optional<OutboxOrder> findById(Long id) {
        return outboxOrderJpaRepository.findById(id);
    }

}
