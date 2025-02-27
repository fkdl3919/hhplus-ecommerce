package kr.hhplus.be.server.infrastructure.jpa.outbox.coupon;

import kr.hhplus.be.server.domain.coupon.outbox.OutboxCoupon;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxCouponJpaRepository extends JpaRepository<OutboxCoupon, Long> {

}
