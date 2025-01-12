package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.application.user.dto.UserInfo;

public record UserResponse(
    long id,
    long point
) {

    public static UserResponse from(UserInfo userInfo) {
        return new UserResponse(userInfo.id(), userInfo.amount());
    }

}
