package com.lecture.authorization.aspect;

import com.lecture.authorization.common.UserInfo;
import com.lecture.auth.repository.AuthRepository;
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
 * 요청이 들어올 때 Bearer 토큰에서 사용자 정보를 추출하여 UserInfo를 생성하고 Request에 저장합니다.
 * 
 * Bearer 토큰이 없으면 X-User-Id 헤더를 사용합니다 (테스트용).
 */
@Component
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {
    
    private final UserGroupMappingService userGroupMappingService;
    private final AuthRepository authRepository;
    
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler)
    {
        Long userId = extractUserIdFromBearerToken(request)
            .orElseGet(() -> extractUserIdFromHeader(request)
                .orElseThrow(() -> new IllegalStateException(
                    "User ID not found. Please provide either 'Authorization: Bearer <token>' header or 'X-User-Id' header."
                )));
        
        // UserGroupMapping에서 groupId 조회
        Long groupId = userGroupMappingService.findByUserId(userId)
            .map(mapping -> mapping.getGroupId())
            .orElse(null);
        
        UserInfo userInfo = new UserInfo(userId, groupId);
        request.setAttribute(UserInfoArgumentResolver.USER_INFO_ATTRIBUTE_NAME, userInfo);
        
        return true;
    }
    
    /**
     * Authorization 헤더에서 Bearer 토큰을 추출하고 검증하여 userId를 반환합니다.
     */
    private Optional<Long> extractUserIdFromBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        return authRepository.findUserIdByToken(token);
    }
    
    /**
     * X-User-Id 헤더에서 userId를 추출합니다 (테스트용).
     */
    private Optional<Long> extractUserIdFromHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(userIdHeader));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X-User-Id header must be a valid number: " + userIdHeader);
        }
    }
}
