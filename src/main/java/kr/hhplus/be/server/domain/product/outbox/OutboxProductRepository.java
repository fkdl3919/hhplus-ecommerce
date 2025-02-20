package kr.hhplus.be.server.domain.product.outbox;

import java.util.Optional;

public interface OutboxProductRepository {

    void save(OutboxProduct outboxProduct);

    Optional<OutboxProduct> findById(Long id);
}
