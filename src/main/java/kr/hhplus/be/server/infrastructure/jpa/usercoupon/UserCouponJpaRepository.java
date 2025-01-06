package kr.hhplus.be.server.infrastructure.jpa.usercoupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.usercoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

}
