package com.lecture.user.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Create User API 요청 DTO
 * 
 * 테스트용 유저 생성을 위한 DTO입니다.
 */
@Getter
@NoArgsConstructor
@Schema(
    description = "유저 생성 요청",
    example = "{\n" +
              "  \"email\": \"test@example.com\",\n" +
              "  \"name\": \"테스트 유저\",\n" +
              "  \"password\": \"password123\",\n" +
              "  \"phoneNumber\": \"01012345678\"\n" +
              "}"
)
public class CreateUserRequest {
    @Schema(description = "이메일", example = "test@example.com", required = true)
    private String email;
    
    @Schema(description = "이름", example = "테스트 유저", required = true)
    private String name;
    
    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;
    
    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String phoneNumber;
}
