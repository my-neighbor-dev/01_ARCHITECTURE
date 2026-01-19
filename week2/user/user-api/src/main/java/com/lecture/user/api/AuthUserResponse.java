package com.lecture.user.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Auth용 User API 응답 DTO
 * 
 * getUserByEmail에서만 사용하며, password를 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {
    private Long id;
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
}
