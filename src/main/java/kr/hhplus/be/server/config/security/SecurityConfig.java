package kr.hhplus.be.server.config.security;

import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.filter.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserFilter userFilter(UserService userService) {
        return new UserFilter(userService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserFilter filter) throws Exception {
        // anyRequest는 인증을 받아야 함
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth ->
                auth.anyRequest().authenticated()
            )
            // BasicAuthenticationFilter 필터 앞에 사용자 정의 filter 추가
            .addFilterBefore(filter, BasicAuthenticationFilter.class);
        return http.build();
    }

}
