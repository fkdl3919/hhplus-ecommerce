package kr.hhplus.be.server.domain.user.info;

import kr.hhplus.be.server.domain.user.User;

public record UserInfo(
    Long id,
    String name
) {

    public static UserInfo of(User user) {
        return new UserInfo(user.getId(), user.getName());
    }

}
