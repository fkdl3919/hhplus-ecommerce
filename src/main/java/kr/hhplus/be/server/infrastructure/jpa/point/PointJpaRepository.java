package kr.hhplus.be.server.infrastructure.jpa.point;

import java.util.Optional;
import kr.hhplus.be.server.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    @Query("select a from Point a where a.userId = :userId")
    Optional<Point> findByUserIdWithVersion(long userId);

}
