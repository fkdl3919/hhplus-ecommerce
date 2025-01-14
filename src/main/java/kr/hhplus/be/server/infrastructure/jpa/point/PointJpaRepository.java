package kr.hhplus.be.server.infrastructure.jpa.point;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import kr.hhplus.be.server.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Point a where a.user.id = :userId")
    Optional<Point> findByUserIdWithLock(long userId);

}
