package kr.hhplus.be.server.infrastructure.jpa.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from Coupon a where a.id = :id")
    Coupon findByIdWithLock(long id);
}
