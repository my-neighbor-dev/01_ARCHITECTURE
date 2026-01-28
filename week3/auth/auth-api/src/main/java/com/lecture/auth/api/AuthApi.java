package com.lecture.auth.api;

import com.lecture.auth.infrastructure.DeviceInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * week3: LoginResponse 추가 (Bearer 토큰 인증을 위해 Access Token 반환)
 */
@Tag(name = "Auth", description = "인증 관련 API")
@RequestMapping("/api/auth")
public interface AuthApi {
    
    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호를 통해 로그인합니다. 성공 시 인증 토큰이 쿠키에 설정되고, 응답 본문에 Access Token이 포함됩니다. Swagger에서 Bearer 토큰으로 사용할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        )
    })
    @PostMapping("/login")
    LoginResponse login(
        @RequestBody LoginRequest request,
        @Parameter(hidden = true) DeviceInfo deviceInfo,
        @Parameter(hidden = true) HttpServletResponse response
    );
}
