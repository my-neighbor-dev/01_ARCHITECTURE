package com.lecture.user.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Auth용 User API 응답 DTO
 * 
 * getUserByEmail에서만 사용하며, password를 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public class AuthUserResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String password;
}
