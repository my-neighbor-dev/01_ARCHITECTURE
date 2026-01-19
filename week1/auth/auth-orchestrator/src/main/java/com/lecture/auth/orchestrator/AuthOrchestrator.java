package com.lecture.auth.orchestrator;

import com.lecture.auth.api.LoginRequest;
import com.lecture.auth.api.LoginResponse;
import com.lecture.auth.domain.LoginResult;
import com.lecture.auth.service.AuthService;
import com.lecture.auth.service.CookieService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Auth Orchestrator
 * 
 * 구조:
 * - Controller -> Orchestrator -> Service -> Repository
 * - Orchestrator는 여러 Service를 조율하여 DTO를 조립합니다.
 * - 비즈니스 로직은 AuthService에서 처리합니다.
 * 
 * 장점:
 * 1. Orchestrator는 조립만 담당하여 단순함
 * 2. 비즈니스 로직은 AuthService에 집중되어 테스트 용이
 * 3. 각 Service는 단일 책임만 수행
 */
@Component
@RequiredArgsConstructor
public class AuthOrchestrator {
    
    private final AuthService authService;  // ✅ 비즈니스 로직 처리
    private final CookieService cookieService;  // ✅ 쿠키 생성
    
    /**
     * 로그인 유스케이스: 여러 Service를 조율하여 DTO 조립
     */
    public LoginResponse login(LoginRequest request) {
        // 1. AuthService를 통해 로그인 처리 (비즈니스 로직: 유저 조회, 비밀번호 검증, 토큰 생성)
        LoginResult loginResult = authService.login(request);
        
        // 2. 쿠키 생성
        Cookie cookie = cookieService.createCookie(loginResult.getAuthToken().getToken());
        
        // 3. DTO 조립
        return new LoginResponse(
            loginResult.getAuthUser().getId(),
            loginResult.getAuthToken().getToken(),
            cookie
        );
    }
}
