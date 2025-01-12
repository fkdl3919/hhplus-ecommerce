package kr.hhplus.be.server.domain.user.info;

import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.User;

public record PointInfo(
    Long id,
    Long userId,
    Long point
) {

    public static PointInfo of(Point point) {
        return new PointInfo(point.getId(), point.getUser().getId(), point.getPoint());
    }

}
