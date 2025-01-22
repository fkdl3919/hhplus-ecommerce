package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {

    Optional<Point> findPointByUserWithVersion(long userId);


    PointHistory saveHistory(PointHistory pointHistory);

    Point save(Point point);
}
