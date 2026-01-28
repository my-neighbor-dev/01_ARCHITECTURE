package com.lecture.authorization.aspect;

import com.lecture.authorization.common.UserInfo;
import com.lecture.group.service.UserGroupMappingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * UserInfoInterceptor
 * 
 * 요청이 들어올 때 UserInfo를 생성하여 Request에 저장합니다.
 * 
 * 실제 프로젝트에서는 SecurityContext나 JWT 토큰에서 사용자 정보를 가져와야 하지만,
 * 여기서는 간단하게 Header에서 userId를 가져오고, UserGroupMapping에서 groupId를 조회합니다.
 */
@Component
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {
    
    private final UserGroupMappingService userGroupMappingService;
    
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler)
    {
        // Header에서 사용자 정보 가져오기 (실제로는 SecurityContext나 JWT에서 가져와야 함)
        String userIdHeader = request.getHeader("X-User-Id");
        Long userId = userIdHeader != null ? Long.parseLong(userIdHeader) : 1L; // 기본값
        
        // UserGroupMapping에서 groupId 조회
        Long groupId = userGroupMappingService.findByUserId(userId)
            .map(mapping -> mapping.getGroupId())
            .orElse(null);
        
        UserInfo userInfo = new UserInfo(userId, groupId);
        request.setAttribute(UserInfoArgumentResolver.USER_INFO_ATTRIBUTE_NAME, userInfo);
        
        return true;
    }
}
