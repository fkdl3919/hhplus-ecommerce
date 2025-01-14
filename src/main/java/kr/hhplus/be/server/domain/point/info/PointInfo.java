package kr.hhplus.be.server.domain.point.info;

import kr.hhplus.be.server.domain.point.Point;

public record PointInfo(
    Long id,
    Long userId,
    Long point
) {

    public static PointInfo of(Point point) {
        return new PointInfo(point.getId(), point.getUserId(), point.getPoint());
    }

}
