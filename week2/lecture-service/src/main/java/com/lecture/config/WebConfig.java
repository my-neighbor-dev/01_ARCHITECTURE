package com.lecture.config;

import com.lecture.auth.infrastructure.DeviceIdInterceptor;
import com.lecture.auth.infrastructure.DeviceInfoArgumentResolver;
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
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceIdInterceptor);
    }
    
    @Override
    public void addArgumentResolvers(java.util.List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(deviceInfoArgumentResolver);
    }
}
