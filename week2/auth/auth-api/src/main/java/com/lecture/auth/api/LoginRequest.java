package com.lecture.auth.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Login API 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(
    description = "로그인 요청",
    example = "{\n" +
              "  \"email\": \"test@example.com\",\n" +
              "  \"password\": \"password123\"\n" +
              "}"
)
public class LoginRequest {
    @Schema(description = "이메일", example = "test@example.com", required = true)
    private String email;
    
    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;
}
