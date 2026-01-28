package com.lecture.auth.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

/**
 * CookieService 구현체
 * 
 * week2 개선: Access Token과 Refresh Token 모두 쿠키로 생성
 */
@Service
public class CookieService {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60; // 1시간
    private static final int REFRESH_TOKEN_MAX_AGE = 24 * 60 * 60; // 24시간

    /**
     * Access Token 쿠키 생성
     */
    public Cookie createAccessTokenCookie(String token) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);  // JavaScript 접근 불가
        cookie.setSecure(true);    // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
        return cookie;
    }

    /**
     * Refresh Token 쿠키 생성
     */
    public Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);  // JavaScript 접근 불가
        cookie.setSecure(true);    // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        return cookie;
    }
}
