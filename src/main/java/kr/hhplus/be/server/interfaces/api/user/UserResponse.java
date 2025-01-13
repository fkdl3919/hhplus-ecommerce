package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.domain.user.info.PointInfo;
import kr.hhplus.be.server.domain.user.info.UserInfo;

public record UserResponse(
    long id,
    long point
) {

    public static UserResponse from(UserInfo userInfo, PointInfo pointInfo) {
        return new UserResponse(userInfo.id(), pointInfo.point());
    }

}
