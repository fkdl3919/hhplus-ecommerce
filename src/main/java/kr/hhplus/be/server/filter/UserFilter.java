package kr.hhplus.be.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import kr.hhplus.be.server.auth.UserAuthentication;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.info.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class UserFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // ?userId=1
        Long userId = validateUserId(request.getParameter("userId"));
        UserInfo user = userService.findUser(userId);
        // 요청의 사용자 인증정보를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        filterChain.doFilter(request, response);
    }

    public Long validateUserId(String paramId) {
        Long userId = null;
        try {
            userId = Long.valueOf(paramId);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            log.error("[Filter] Invalid user id {}", paramId);
            throw new SecurityException("유저정보가 올바르지 않습니다.", e);
        }
        return userId;
    }
}
