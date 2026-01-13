package com.lecture.auth.api;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

/**
 * Login API 응답 DTO
 * 
 * User 도메인 모델과 분리된 순수한 DTO입니다.
 * 변환 로직은 Orchestrator에서 처리합니다.
 */

@RequiredArgsConstructor
public class LoginResponse {
    private final Long userId;
    private final String token;
    private final Cookie cookie;
}
