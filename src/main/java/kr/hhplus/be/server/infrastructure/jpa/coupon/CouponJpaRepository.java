package kr.hhplus.be.server.infrastructure.jpa.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<User, Long> {

}
