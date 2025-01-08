package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.user.enums.PointTransactionType;
import kr.hhplus.be.server.domain.user.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public Long userPoint(long id) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Point point = pointRepository.findPointByUserIdWithLock(id).orElse(Point.emptyPoint(null));
        return point.getPoint();
    }

    @Transactional
    public void chargePoint(long id, long amount) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Point point = pointRepository.findPointByUserIdWithLock(user.getId()).orElse(Point.emptyPoint(user));

        // point 도메인에 책임
        point.charge(amount);

        // 충전 후 포인트 히스토리 테이블 입력
        PointHistory pointHistory = PointHistory.builder()
            .amount(amount)
            .type(PointTransactionType.CHARGE)
            .updatedPoint(point.getPoint())
            .build();

        pointHistoryRepository.save(pointHistory);
    }
}
