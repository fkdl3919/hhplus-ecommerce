package kr.hhplus.be.server.domain.order.outbox;

import java.util.Optional;

public interface OutboxOrderRepository {

    void save(OutboxOrder outboxOrder);

    Optional<OutboxOrder> findById(Long id);
}
