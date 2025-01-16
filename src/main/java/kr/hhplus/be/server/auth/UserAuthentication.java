package kr.hhplus.be.server.auth;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAuthentication extends AbstractAuthenticationToken {

    private UserInfo userInfo;

    public UserAuthentication(UserInfo userInfo) {
        super(
            // 권한정보
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        this.userInfo = userInfo;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        // 인증수단
        return userInfo.id();
    }

    @Override
    public Object getPrincipal() {
        // 현재 인증된 사용자를 나타내는 객체
        return userInfo;
    }

}
