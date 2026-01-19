package com.lecture.auth.domain;

import lombok.Getter;

/**
 * AuthToken 도메인 모델
 * 
 * 인증 토큰 정보를 담는 도메인 객체입니다.
 */
@Getter
public class AuthToken {
    private String token;
    private Long userId;
    private Long expiresAt;
    
    public AuthToken(String token, Long userId, Long expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }
}
