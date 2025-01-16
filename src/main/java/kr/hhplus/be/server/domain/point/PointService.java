package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.point.command.PointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;


    @Transactional
    public Long userPoint(long userId) {
        Point point = pointRepository.findPointByUserIdWithLock(userId).orElseThrow(() -> new EntityNotFoundException("사용자의 포인트가 존재하지 않습니다."));
        return point.getPoint();
    }

    @Transactional
    public void chargePoint(PointCommand.Charge command) {
        Point point = pointRepository.findPointByUserIdWithLock(command.userId()).orElseThrow(() -> new EntityNotFoundException("사용자의 포인트가 존재하지 않습니다."));

        // point 충전 후 history 반환
        PointHistory pointHistory = point.charge(command.point());

        pointRepository.saveHistory(pointHistory);
    }

    @Transactional
    public Point use(PointCommand.Use command) {
        Point point = pointRepository.findPointByUserIdWithLock(command.userId()).orElseThrow(() -> new EntityNotFoundException("사용자의 포인트가 존재하지 않습니다."));

        // point 사용 후 history 반환
        PointHistory pointHistory = point.use(command.point());

        pointRepository.saveHistory(pointHistory);

        return point;
    }

}
