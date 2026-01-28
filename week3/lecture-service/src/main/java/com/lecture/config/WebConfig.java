package com.lecture.config;

import com.lecture.auth.infrastructure.DeviceIdInterceptor;
import com.lecture.auth.infrastructure.DeviceInfoArgumentResolver;
import com.lecture.authorization.aspect.UserInfoArgumentResolver;
import com.lecture.authorization.aspect.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 * 
 * Interceptor와 ArgumentResolver를 등록합니다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final DeviceIdInterceptor deviceIdInterceptor;
    private final DeviceInfoArgumentResolver deviceInfoArgumentResolver;
    private final UserInfoInterceptor userInfoInterceptor;
    private final UserInfoArgumentResolver userInfoArgumentResolver;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceIdInterceptor);
        registry.addInterceptor(userInfoInterceptor)
            .excludePathPatterns(
                // Swagger UI 및 문서
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api-docs/**",
                // H2 콘솔
                "/h2-console/**",
                // 에러 페이지 및 정적 리소스
                "/error",
                "/favicon.ico",
                // 인증이 필요 없는 API
                "/api/users",  // POST - 유저 생성
                "/api/users/by-email/**",  // GET - 이메일로 유저 조회 (인증용)
                "/api/groups",  // POST - 그룹 생성
                "/api/groups/*/users",  // POST - 유저를 그룹에 추가
                "/api/lectures",  // POST - 강의 생성
                "/api/auth/**"  // 인증 관련 API (로그인 등)
            );
    }
    
    @Override
    public void addArgumentResolvers(java.util.List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(deviceInfoArgumentResolver);
        resolvers.add(userInfoArgumentResolver);
    }
}
