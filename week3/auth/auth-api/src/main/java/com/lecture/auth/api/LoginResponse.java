package com.lecture.auth.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * LoginResponse
 * 
 * 로그인 성공 시 반환되는 응답입니다.
 * Access Token을 포함하여 Swagger에서 Bearer 토큰으로 사용할 수 있습니다.
 */
@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답")
public class LoginResponse {
    
    @Schema(description = "Access Token (Bearer 토큰으로 사용)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String accessToken;
    
    @Schema(description = "Refresh Token", example = "550e8400-e29b-41d4-a716-446655440001")
    private String refreshToken;
}
