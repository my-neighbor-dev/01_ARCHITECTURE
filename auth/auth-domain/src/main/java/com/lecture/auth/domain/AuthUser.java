package com.lecture.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AuthUser 도메인 모델
 * 
 * Auth 모듈에서 사용하는 User 정보입니다.
 * User 도메인과 분리되어 Auth 모듈에 필요한 정보만 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public class AuthUser {
    private final Long id;
    private final String email;
    private final String password;
}
