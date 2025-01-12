package kr.hhplus.be.server.infrastructure.jpa.user.point;

import java.util.Optional;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PointRepositoryImpl implements PointRepository {

    private PointJpaRepository pointJpaRepository;
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Optional<Point> findPointByUserIdWithLock(long userId) {
        return pointJpaRepository.findByUserIdWithLock(userId);
    }

    @Override
    public PointHistory saveHistory(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }

}
