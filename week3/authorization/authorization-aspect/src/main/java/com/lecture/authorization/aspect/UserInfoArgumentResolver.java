package com.lecture.authorization.aspect;

import com.lecture.authorization.common.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * UserInfoArgumentResolver
 * 
 * Controller 메서드의 UserInfo 파라미터를 자동으로 주입합니다.
 * UserInfoInterceptor에서 Request에 저장한 UserInfo를 가져옵니다.
 */
@Component
public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {
    
    public static final String USER_INFO_ATTRIBUTE_NAME = "userInfo";
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == UserInfo.class;
    }
    
    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory)
    {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        UserInfo userInfo = (UserInfo) request.getAttribute(USER_INFO_ATTRIBUTE_NAME);
        
        if (userInfo == null) {
            throw new IllegalStateException("UserInfo not found in request. UserInfoInterceptor must be registered.");
        }
        
        return userInfo;
    }
}
