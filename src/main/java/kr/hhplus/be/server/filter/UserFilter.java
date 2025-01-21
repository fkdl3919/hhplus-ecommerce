package kr.hhplus.be.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.hhplus.be.server.auth.UserAuthentication;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.auth.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class UserFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final String KEY = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // ?userId=1
        UserInfo user = getUser(request);
        // 요청의 사용자 인증정보를 SecurityContext에 저장
        if(user != null) {
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        }
        filterChain.doFilter(request, response);
    }

    public UserInfo getUser(HttpServletRequest request) {
        String paramId = request.getParameter(KEY);

        if (paramId == null) return null;

        Long userId = null;
        try {
            userId = Long.valueOf(paramId);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            log.error("[Filter] Invalid user id {}", paramId);
            throw new SecurityException("유저정보가 올바르지 않습니다.", e);
        }
        return userService.findUser(userId);
    }
}
