package kr.hhplus.be.server.infrastructure.jpa.outbox.order;

import kr.hhplus.be.server.domain.order.outbox.OutboxOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxOrderJpaRepository extends JpaRepository<OutboxOrder, Long> {

}
