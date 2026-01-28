package com.lecture.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * LoginResult 도메인 모델
 * 
 * 로그인 처리 결과를 담는 도메인 객체입니다.
 * week2: Access Token과 Refresh Token 모두 포함
 */
@Getter
@RequiredArgsConstructor
public class LoginResult {
    private final AuthToken accessToken;
    private final AuthToken refreshToken;
}
