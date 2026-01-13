package com.lecture.user.orchestrator;

import com.lecture.user.api.AuthUserResponse;
import com.lecture.user.api.UserResponse;
import com.lecture.user.domain.User;
import com.lecture.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * User Orchestrator
 * 
 * Controller와 Service 사이의 계층으로, 비즈니스 유스케이스를 조율합니다.
 * 
 * 구조:
 * - Controller -> Orchestrator -> Service -> Repository
 * 
 * 장점:
 * 1. Controller는 HTTP 요청/응답 처리에만 집중
 * 2. Orchestrator가 여러 Service를 조율하여 유스케이스 완성
 * 3. Service는 단일 책임만 수행 (재사용 가능)
 * 4. DTO 변환 로직을 Orchestrator에서 처리
 */
@Service
@RequiredArgsConstructor
public class UserOrchestrator {
    
    private final UserService userService;

    public UserResponse getUserById(Long id) {
        User user = userService.getUserById(id);
        return toUserResponse(user);
    }

    public AuthUserResponse getUserByEmail(String email) {
        User user = userService.getUserByEmail(email);
        return toAuthUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
    
    private AuthUserResponse toAuthUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new AuthUserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPassword()
        );
    }
}
