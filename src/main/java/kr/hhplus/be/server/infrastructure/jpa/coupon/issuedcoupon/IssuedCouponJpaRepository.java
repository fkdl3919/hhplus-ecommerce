package kr.hhplus.be.server.infrastructure.jpa.coupon.issuedcoupon;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

}
