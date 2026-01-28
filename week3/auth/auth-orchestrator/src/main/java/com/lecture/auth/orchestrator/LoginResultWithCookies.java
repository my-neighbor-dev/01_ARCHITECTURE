package com.lecture.auth.orchestrator;

import com.lecture.auth.domain.AuthToken;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * LoginResultWithCookies
 * 
 * 쿠키와 토큰 정보를 반환하기 위한 래퍼 클래스입니다.
 * Response Body에 토큰을 포함하고 쿠키도 설정합니다.
 */
@Getter
@RequiredArgsConstructor
public class LoginResultWithCookies {
    private final Cookie accessTokenCookie;
    private final Cookie refreshTokenCookie;
    private final AuthToken accessToken;
    private final AuthToken refreshToken;
}
