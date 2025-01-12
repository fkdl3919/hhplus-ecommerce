package kr.hhplus.be.server.infrastructure.jpa.user.point;

import kr.hhplus.be.server.domain.user.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {

}
