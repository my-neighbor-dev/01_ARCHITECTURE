package com.lecture.user.api;

/**
 * User API 응답 DTO
 * 
 * User 도메인 모델과 분리된 순수한 DTO입니다.
 * 변환 로직은 Orchestrator에서 처리합니다.
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponse {
    private final Long id;
    private final String email;
    private final String name;
}
