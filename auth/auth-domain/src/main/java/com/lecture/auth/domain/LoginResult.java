package com.lecture.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * LoginResult 도메인 모델
 * 
 * 로그인 처리 결과를 담는 도메인 객체입니다.
 */
@Getter
@RequiredArgsConstructor
public class LoginResult {
    private final AuthUser authUser;
    private final AuthToken authToken;
}
