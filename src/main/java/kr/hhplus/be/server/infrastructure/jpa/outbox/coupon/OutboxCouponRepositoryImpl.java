package kr.hhplus.be.server.infrastructure.jpa.outbox.coupon;

import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.outbox.OutboxCoupon;
import kr.hhplus.be.server.domain.coupon.outbox.OutboxCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxCouponRepositoryImpl implements OutboxCouponRepository {

    private final OutboxCouponJpaRepository outboxCouponJpaRepository;

    @Override
    public void save(OutboxCoupon outboxCoupon) {
        outboxCouponJpaRepository.save(outboxCoupon);
    }

    @Override
    public Optional<OutboxCoupon> findById(Long id) {
        return outboxCouponJpaRepository.findById(id);
    }

}
