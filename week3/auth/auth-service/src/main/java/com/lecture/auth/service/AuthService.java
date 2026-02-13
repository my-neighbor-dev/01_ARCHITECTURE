package com.lecture.auth.service;

import com.lecture.auth.domain.AuthToken;
import com.lecture.auth.domain.AuthUser;
import com.lecture.auth.domain.LoginResult;
import com.lecture.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Auth Service
 * 
 * 인증 관련 비즈니스 로직을 처리합니다.
 * 
 * 구조:
 * - Orchestrator -> AuthService
 * 
 * 책임:
 * 1. 비밀번호 검증
 * 2. Access Token 생성
 * 3. Refresh Token 생성 및 저장
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long MINUTES_PER_HOUR = 60L;
    private static final long HOURS_PER_DAY = 24L;
    private static final long ACCESS_TOKEN_EXPIRY_MILLIS = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND; // 1시간
    private static final long REFRESH_TOKEN_EXPIRY_MILLIS = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
            * MILLIS_PER_SECOND; // 24시간

    private final AuthRepository authRepository;

    /**
     * 로그인 처리: 비밀번호 검증 및 토큰 생성
     */
    public LoginResult login(AuthUser authUser, String password) {
        // 1. 비밀번호 검증
        if (!authUser.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // 2. Access Token 생성 및 저장 (Bearer 토큰 인증을 위해 저장)
        AuthToken accessToken = generateAccessToken(authUser);
        authRepository.deleteAccessTokenByUserId(authUser.getId());
        authRepository.saveAccessToken(accessToken);

        // 3. Refresh Token 생성 및 저장 (도메인 객체를 직접 전달)
        AuthToken refreshToken = generateRefreshToken(authUser);
        authRepository.deleteRefreshTokenByUserId(authUser.getId());
        authRepository.saveRefreshToken(refreshToken);

        return new LoginResult(accessToken, refreshToken);
    }

    /**
     * Access Token 생성 (1시간 유효)
     */
    private AuthToken generateAccessToken(AuthUser authUser) {
        String token = java.util.UUID.randomUUID().toString();
        Long expiresAt = System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY_MILLIS;
        return new AuthToken(token, authUser.getId(), expiresAt);
    }

    /**
     * Refresh Token 생성 (24시간 유효)
     */
    private AuthToken generateRefreshToken(AuthUser authUser) {
        String token = java.util.UUID.randomUUID().toString();
        Long expiresAt = System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_MILLIS;
        return new AuthToken(token, authUser.getId(), expiresAt);
    }
}
