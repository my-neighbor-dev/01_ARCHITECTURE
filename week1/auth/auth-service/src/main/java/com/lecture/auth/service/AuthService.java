package com.lecture.auth.service;

import com.lecture.auth.api.LoginRequest;
import com.lecture.auth.domain.AuthToken;
import com.lecture.auth.domain.AuthUser;
import com.lecture.auth.domain.LoginResult;
import com.lecture.auth.external.api.AuthUserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Auth Service
 * 
 * 인증 관련 비즈니스 로직을 처리합니다.
 * 
 * 구조:
 * - Orchestrator -> AuthService -> AuthUserApi
 * 
 * 책임:
 * 1. 유저 조회
 * 2. 비밀번호 검증
 * 3. 토큰 생성
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthUserApi authUserApi;

    public LoginResult login(LoginRequest request) {
        // 1. 유저 조회
        AuthUser authUser = authUserApi.getUserByEmail(request.getEmail());
        if (authUser == null) {
            throw new RuntimeException("User not found");
        }
        
        // 2. 비밀번호 검증
        if (!authUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // 3. 토큰 생성
        AuthToken authToken = generateToken(authUser);
        
        return new LoginResult(authUser, authToken);
    }
    
    /**
     * 토큰 생성
     */
    private AuthToken generateToken(AuthUser authUser) {
        String token = java.util.UUID.randomUUID().toString();
        Long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24시간
        return new AuthToken(token, authUser.getId(), expiresAt);
    }
}
