package kr.hhplus.be.server.domain.coupon.outbox;

import java.util.Optional;

public interface OutboxCouponRepository {

    void save(OutboxCoupon outboxCoupon);

    Optional<OutboxCoupon> findById(Long id);
}
