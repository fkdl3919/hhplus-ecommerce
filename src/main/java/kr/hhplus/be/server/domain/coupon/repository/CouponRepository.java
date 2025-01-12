package kr.hhplus.be.server.domain.coupon.repository;

import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepository {

    Optional<Coupon> findByIdWithLock(long id);

    Optional<Coupon> findById(long couponId);

    IssuedCoupon saveIssuedCoupon(IssuedCoupon issuedCoupon);

    Page<IssuedCoupon> selectIssuedCouponList(long userId, Pageable pageable);

    Optional<IssuedCoupon> findIssuedCouponById(long issuedCouponId);
}
