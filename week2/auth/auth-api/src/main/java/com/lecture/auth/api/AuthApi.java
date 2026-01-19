package com.lecture.auth.api;

import com.lecture.auth.infrastructure.DeviceInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Auth API 인터페이스
 * 
 * 이 인터페이스는 인증 관련 API 스펙을 정의합니다.
 *
 * week2: HttpServletResponse 추가 (쿠키 설정용)
 * week2: DeviceInfo 파라미터 추가 (ArgumentResolver로 주입)
 */
@Tag(name = "Auth", description = "인증 관련 API")
@RequestMapping("/api/auth")
public interface AuthApi {
    
    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호를 통해 로그인합니다. 성공 시 인증 토큰이 쿠키에 설정됩니다."
    )
    @PostMapping("/login")
    void login(
        @RequestBody LoginRequest request,
        @Parameter(hidden = true) DeviceInfo deviceInfo,
        @Parameter(hidden = true) HttpServletResponse response
    );
}
