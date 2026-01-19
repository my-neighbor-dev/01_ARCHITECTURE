package com.lecture.auth.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

/**
 * CookieService 구현체
 */
@Service
public class CookieService {

    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24시간
        return cookie;
    }
}
