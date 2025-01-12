package kr.hhplus.be.server.infrastructure.jpa.user.pointhistory;

import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.PointHistory.PointHistoryBuilder;
import kr.hhplus.be.server.domain.user.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository jpaRepository;

    @Override
    public PointHistory save(PointHistory pointHistory) {
        return jpaRepository.save(pointHistory);
    }
}
