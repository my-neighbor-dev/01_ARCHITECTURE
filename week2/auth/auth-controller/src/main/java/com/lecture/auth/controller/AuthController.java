package com.lecture.auth.controller;

import com.lecture.auth.api.AuthApi;
import com.lecture.auth.api.LoginRequest;
import com.lecture.auth.infrastructure.DeviceInfo;
import com.lecture.auth.orchestrator.AuthOrchestrator;
import com.lecture.auth.orchestrator.LoginResultWithCookies;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth API 구현체
 * 
 * AuthApi 인터페이스를 구현하여 실제 HTTP 요청을 처리합니다.
 * 
 * 구조:
 * - API -> Controller -> Orchestrator -> Service -> Repository
 * 
 * week2 개선:
 * - 쿠키를 Response에 설정
 * - DeviceInfo 파라미터 추가 (ArgumentResolver로 주입)
 * - LoginResponse 제거 (쿠키만으로 충분)
 */
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    
    private final AuthOrchestrator authOrchestrator;
    
    @Override
    public void login(LoginRequest request, DeviceInfo deviceInfo, HttpServletResponse response) {
        LoginResultWithCookies result = authOrchestrator.login(request, deviceInfo);
        
        // 쿠키를 Response에 추가
        response.addCookie(result.getAccessTokenCookie());
        response.addCookie(result.getRefreshTokenCookie());
        
        // Response Body는 비움 (쿠키만으로 충분)
    }
}
