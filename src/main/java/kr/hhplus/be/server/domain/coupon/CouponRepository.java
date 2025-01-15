package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepository {

    Optional<Coupon> findByIdWithLock(long id);

    Optional<Coupon> findById(Long couponId);

    IssuedCoupon saveIssuedCoupon(IssuedCoupon issuedCoupon);

    Page<IssuedCoupon> selectIssuedCouponList(long userId, Pageable pageable);

    Optional<IssuedCoupon> findIssuedCouponById(Long issuedCouponId);

    Coupon save(Coupon coupon);
}
