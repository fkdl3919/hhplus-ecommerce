package kr.hhplus.be.server.resolver;

import kr.hhplus.be.server.auth.UserProvider;
import kr.hhplus.be.server.auth.UserAuthentication;
import kr.hhplus.be.server.auth.UserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 컨트롤러 메서드의 파라미터를 커스텀하게 주입할 때 사용하는 인터페이스
 * 요청을 처리하는 메서드의 특정 파라미터를 자동으로 변환하고 주입하는 역할
 */
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 특정 파라미터 타입을 처리할 수 있는지 확인
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        // 메소드 파라미터에 미리 정의한 어노테이션이 존재하는지 확인
        boolean isResolver = parameter.hasParameterAnnotation(UserProvider.class);
        // 해당 파라미터의 타입 확인
        boolean isAuthUser = UserInfo.class.equals(parameter.getParameterType());
        return isResolver && isAuthUser;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // filter에서 등록해준 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
            authentication instanceof UserAuthentication &&
            authentication.isAuthenticated()
        ) {
            return authentication.getPrincipal();
        }
        return null;
    }
}
