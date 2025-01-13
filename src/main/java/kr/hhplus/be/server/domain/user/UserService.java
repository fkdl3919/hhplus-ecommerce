package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.user.info.PointInfo;
import kr.hhplus.be.server.domain.user.info.UserInfo;
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

    @Transactional
    public User findUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        return user;
    }

    @Transactional
    public Point findPoint(long id) {
        Point point = pointRepository.findPointByUserIdWithLock(id).orElseThrow(() -> new EntityNotFoundException("사용자의 포인트가 존재하지 않습니다."));
        return point;
    }

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

        // point 충전 후 history 반환
        PointHistory pointHistory = point.charge(amount);

        pointRepository.saveHistory(pointHistory);
    }

    @Transactional
    public PointHistory use(PointHistory pointHistory) {
        return pointRepository.saveHistory(pointHistory);
    }
}
