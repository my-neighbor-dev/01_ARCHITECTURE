package com.lecture.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Auth API 인터페이스
 * 
 * 이 인터페이스는 인증 관련 API 스펙을 정의합니다.
 *
 * 장점:
 * 1. API 스펙이 명확하게 정의됨 (Swagger 어노테이션)
 * 2. 다른 서비스에서 이 인터페이스만 의존하면 API 스펙을 알 수 있음
 * 3. Controller는 이 인터페이스를 구현하여 Swagger를 의존할 필요가 없음
 * 4. API 문서 자동 생성 (Swagger UI)
 */
@Tag(name = "Auth", description = "인증 관련 API")
@RequestMapping("/api/auth")
public interface AuthApi {
    
    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호를 통해 로그인합니다. 성공 시 인증 토큰이 쿠키에 설정됩니다."
    )
    @PostMapping("/login")
    LoginResponse login(
        @RequestBody LoginRequest request
    );
}
