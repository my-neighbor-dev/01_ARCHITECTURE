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
        registry.addInterceptor(userInfoInterceptor);
    }
    
    @Override
    public void addArgumentResolvers(java.util.List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(deviceInfoArgumentResolver);
        resolvers.add(userInfoArgumentResolver);
    }
}
