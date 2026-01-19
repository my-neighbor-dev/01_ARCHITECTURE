package com.lecture.auth.external.api;

import com.lecture.auth.domain.AuthUser;

/**
 * Auth용 User API 인터페이스
 * 
 * Auth 모듈에서 User 모듈의 API를 호출하기 위한 인터페이스입니다.
 * 
 * 구조:
 * - AuthOrchestrator -> AuthUserApi (인터페이스) -> AuthUserApiFeign (Feign 구현체)
 * 
 * 장점:
 * 1. 인터페이스로 정의하여 구현체를 유연하게 교체 가능
 * 2. Feign 구현체를 통해 다른 서비스의 UserApi 호출
 * 3. 테스트 시 Mock 객체 주입 용이
 * 4. AuthUser 도메인만 참조하여 user-domain 의존성 제거
 */
public interface AuthUserApi {
    AuthUser getUserByEmail(String email);
}
