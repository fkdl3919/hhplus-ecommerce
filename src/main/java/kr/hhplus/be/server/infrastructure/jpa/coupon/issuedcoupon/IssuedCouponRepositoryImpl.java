package kr.hhplus.be.server.infrastructure.jpa.coupon.issuedcoupon;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return issuedCouponJpaRepository.save(issuedCoupon);
    }
}
