package com.lecture.auth.external.api.feign;

import com.lecture.auth.domain.AuthUser;
import com.lecture.auth.external.api.AuthUserApi;
import com.lecture.user.api.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * AuthUserApi의 Feign 구현체
 * 
 * Auth 모듈에서 User 모듈의 API를 호출할 때 사용합니다.
 * 
 * 구조:
 * - AuthOrchestrator -> AuthUserApi (인터페이스) -> AuthUserApiFeign (이 클래스)
 * - Feign Client: UserApiFeignClient (UserApi를 호출)
 * 
 * 장점:
 * 1. Service 인터페이스를 통해 User 조회 기능 사용
 * 2. Feign을 통해 다른 서비스의 API 호출
 * 3. DTO를 AuthUser Domain 객체로 변환하여 user-domain 의존성 제거
 */
@Component
@RequiredArgsConstructor
public class AuthUserApiFeign implements AuthUserApi {
    
    private final UserApiFeignClient userApiFeignClient;
    
    @Override
    public AuthUser getUserByEmail(String email) {
        AuthUserResponse authUserResponse = userApiFeignClient.getUserByEmail(email);
        return toAuthUser(authUserResponse);
    }
    
    /**
     * AuthUserResponse DTO를 AuthUser Domain 객체로 변환
     */
    private AuthUser toAuthUser(AuthUserResponse authUserResponse) {
        if (authUserResponse == null) {
            return null;
        }
        return new AuthUser(
            authUserResponse.getId(),
            authUserResponse.getEmail(),
            authUserResponse.getPassword()
        );
    }

    @FeignClient(name = "lecture-service", url = "${lecture.service.url:http://localhost:8080}")
    public interface UserApiFeignClient {
        @GetMapping("/api/users/by-email/{email}")
        AuthUserResponse getUserByEmail(@PathVariable("email") String email);
    }
}
