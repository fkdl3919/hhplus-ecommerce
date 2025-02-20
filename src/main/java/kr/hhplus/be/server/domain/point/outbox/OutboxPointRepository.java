package kr.hhplus.be.server.domain.point.outbox;

import java.util.Optional;

public interface OutboxPointRepository {

    void save(OutboxPoint outboxPoint);

    Optional<OutboxPoint> findById(Long id);
}
