package com.lecture.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User API 인터페이스
 * 
 * 이 인터페이스는 API 스펙을 정의합니다.
 * 
 * 장점:
 * 1. API 스펙이 명확하게 정의됨 (Swagger 어노테이션)
 * 2. 다른 서비스에서 이 인터페이스만 의존하면 API 스펙을 알 수 있음
 * 3. Controller는 이 인터페이스를 구현하여 Swagger를 의존할 필요가 없음
 * 4. API 문서 자동 생성 (Swagger UI)
 */
@Tag(name = "User", description = "유저 관련 API")
@RequestMapping("/api/users")
public interface UserApi {
    
    @Operation(
        summary = "유저 정보 조회",
        description = "유저 ID를 통해 유저 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    UserResponse getUserById(
        @Parameter(description = "유저 ID", required = true, example = "1")
        @PathVariable Long id
    );
    
    @Operation(
        summary = "유저 정보 조회 (이메일)",
        description = "이메일을 통해 유저 정보를 조회합니다. (인증용 - password 포함)"
    )
    @GetMapping("/by-email/{email}")
    AuthUserResponse getUserByEmail(
        @Parameter(description = "이메일", required = true, example = "user@example.com")
        @PathVariable String email
    );
}
