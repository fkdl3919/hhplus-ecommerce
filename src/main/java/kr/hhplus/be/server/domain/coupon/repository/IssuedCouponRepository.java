package kr.hhplus.be.server.domain.coupon.repository;

import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IssuedCouponRepository {

    IssuedCoupon save(IssuedCoupon issuedCoupon);

    Page<IssuedCoupon> selectIssuedCouponList(long userId, Pageable pageable);

    Optional<IssuedCoupon> findById(long issuedCouponId);
}
