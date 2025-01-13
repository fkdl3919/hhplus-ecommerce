package kr.hhplus.be.server.domain.user.repository;

import java.util.Optional;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.User;

public interface PointRepository {

    Optional<Point> findPointByUserIdWithLock(long userId);


    PointHistory saveHistory(PointHistory pointHistory);

    Point save(Point point);
}
