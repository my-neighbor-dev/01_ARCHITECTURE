package com.lecture.auth.orchestrator;

import com.lecture.auth.api.LoginRequest;
import com.lecture.auth.domain.AuthUser;
import com.lecture.auth.domain.LoginResult;
import com.lecture.auth.external.api.AuthUserApi;
import com.lecture.auth.infrastructure.DeviceInfo;
import com.lecture.auth.service.RateLimitingService;
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
 * week2 개선:
 * - Rate Limiting 체크 추가
 * - DeviceInfo 파라미터 추가
 * - 쿠키 생성 및 반환
 * - 유저 조회 로직을 Orchestrator로 이동
 */
@Component
@RequiredArgsConstructor
public class AuthOrchestrator {

    private final RateLimitingService rateLimitingService;  // ✅ Rate Limiting 체크
    private final AuthService authService;  // ✅ 비즈니스 로직 처리
    private final CookieService cookieService;  // ✅ 쿠키 생성
    private final AuthUserApi authUserApi;  // ✅ 유저 조회
    
    /**
     * 로그인 유스케이스: 여러 Service를 조율하여 DTO 조립
     */
    public LoginResultWithCookies login(LoginRequest request, DeviceInfo deviceInfo) {
        // 1. 유저 조회 (email로 조회)
        AuthUser authUser = authUserApi.getUserByEmail(request.getEmail());
        
        // 2. Rate Limit 체크 (유저 조회 후 phoneNumber로 체크)
        rateLimitingService.checkRateLimit(authUser.getPhoneNumber(), deviceInfo);
        
        // 3. AuthService를 통해 로그인 처리 (비즈니스 로직: 비밀번호 검증, 토큰 생성)
        LoginResult loginResult = authService.login(authUser, request.getPassword());
        
        // 4. 로그인 성공 시 Rate Limit 카운트 리셋 (정상 사용자이므로 제한 해제)
        rateLimitingService.resetRateLimit(authUser.getPhoneNumber(), deviceInfo);
        
        // 5. 쿠키 생성
        Cookie accessTokenCookie = cookieService.createAccessTokenCookie(
            loginResult.getAccessToken().getToken()
        );
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(
            loginResult.getRefreshToken().getToken()
        );
        
        // 6. 쿠키만 반환 (Response Body는 비움)
        return new LoginResultWithCookies(accessTokenCookie, refreshTokenCookie);
    }
}
