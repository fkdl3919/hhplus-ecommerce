package kr.hhplus.be.server.infrastructure.jpa.usercoupon;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJpaRepository extends JpaRepository<User, Long> {

}
