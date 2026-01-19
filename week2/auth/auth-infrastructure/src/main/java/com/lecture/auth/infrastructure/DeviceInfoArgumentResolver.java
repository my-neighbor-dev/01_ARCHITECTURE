package com.lecture.auth.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * DeviceInfoArgumentResolver
 * 
 * Controller 메서드의 DeviceInfo 파라미터를 자동으로 주입합니다.
 * DeviceIdInterceptor에서 Request에 저장한 DeviceInfo를 가져옵니다.
 */
@Component
public class DeviceInfoArgumentResolver implements HandlerMethodArgumentResolver {
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == DeviceInfo.class;
    }
    
    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory)
    {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        DeviceInfo deviceInfo = (DeviceInfo) request.getAttribute(
            DeviceIdInterceptor.DEVICE_INFO_ATTRIBUTE_NAME
        );
        
        return deviceInfo;
    }
}
