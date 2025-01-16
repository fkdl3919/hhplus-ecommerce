package kr.hhplus.be.server.config.web;

import java.util.List;
import kr.hhplus.be.server.resolver.UserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 사용자 정의 argument resolver 등록
        resolvers.add(new UserArgumentResolver());
    }
}
