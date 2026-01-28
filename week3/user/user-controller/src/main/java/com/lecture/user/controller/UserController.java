package com.lecture.user.controller;

import com.lecture.user.api.AuthUserResponse;
import com.lecture.user.api.CreateUserRequest;
import com.lecture.user.api.UserApi;
import com.lecture.user.api.UserResponse;
import com.lecture.user.orchestrator.UserOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * User API 구현체
 * 
 * UserApi 인터페이스를 구현하여 실제 HTTP 요청을 처리합니다.
 * 
 * 구조:
 * - API -> Controller -> Orchestrator -> Service -> Repository
 * 
 * 장점:
 * 1. API 인터페이스에 정의된 스펙을 그대로 구현
 * 2. Controller에 복잡하게 있던 Swagger 관련 코드들이 분리됨
 * 3. Controller에서는 로깅, 권한 체크 등의 로직만 수행
 * 4. Orchestrator를 통해 비즈니스 로직 처리
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    
    private final UserOrchestrator userOrchestrator;
    
    @Override
    public UserResponse getUserById(Long id) {
        return userOrchestrator.getUserById(id);
    }
    
    @Override
    public AuthUserResponse getUserByEmail(String email) {
        return userOrchestrator.getUserByEmail(email);
    }
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        return userOrchestrator.createUser(
            request.getEmail(),
            request.getName(),
            request.getPassword(),
            request.getPhoneNumber()
        );
    }
}
