package com.lecture.auth.orchestrator;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * LoginResultWithCookies
 * 
 * 쿠키를 반환하기 위한 래퍼 클래스입니다.
 * Response Body는 비우고 쿠키만 전송합니다.
 */
@Getter
@RequiredArgsConstructor
public class LoginResultWithCookies {
    private final Cookie accessTokenCookie;
    private final Cookie refreshTokenCookie;
}
