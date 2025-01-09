package kr.hhplus.be.server.infrastructure.jpa.coupon;

import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findByIdWithLock(long id) {
        return Optional.of(couponJpaRepository.findByIdWithLock(id));
    }
}
