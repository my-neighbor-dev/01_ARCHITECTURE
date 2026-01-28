package com.lecture.authorization.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 정보를 담는 도메인 객체
 * 
 * Controller에서 UserInfo 파라미터로 주입받아 사용합니다.
 * DeviceInfo와 유사한 방식으로 ArgumentResolver를 통해 주입됩니다.
 */
@Getter
@RequiredArgsConstructor
public class UserInfo {
    private final Long userId;
    private final Long groupId;  // 사용자가 속한 그룹 ID (nullable)
}
