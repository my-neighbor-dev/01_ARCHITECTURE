package com.lecture.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정
 * 
 * Bearer 토큰 인증과 X-User-Id 헤더를 위한 SecurityScheme을 정의합니다.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Lecture Service API",
        description = "강의 서비스 API 문서",
        version = "1.0.0"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Bearer 토큰을 입력하세요. 로그인 API를 통해 받은 Access Token을 사용합니다."
)
@SecurityScheme(
    name = "userIdHeader",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = "X-User-Id",
    description = "테스트용 사용자 ID 헤더입니다. 숫자로 사용자 ID를 입력하세요 (예: 1, 2)."
)
public class SwaggerConfig {
}
