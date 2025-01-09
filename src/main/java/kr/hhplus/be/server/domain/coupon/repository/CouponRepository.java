package kr.hhplus.be.server.domain.coupon.repository;

import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;

public interface CouponRepository {

    Optional<Coupon> findByIdWithLock(long id);

    Optional<Coupon> findById(long couponId);
}
