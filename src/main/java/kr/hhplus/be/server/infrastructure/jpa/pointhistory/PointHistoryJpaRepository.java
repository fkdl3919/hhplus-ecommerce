package kr.hhplus.be.server.infrastructure.jpa.pointhistory;

import kr.hhplus.be.server.domain.pointhistory.PointHistory;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {

}
