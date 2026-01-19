package com.lecture.auth.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DeviceInfo 도메인 모델
 * 
 * 사용자 장치 정보를 담는 도메인 객체입니다.
 * DeviceIdInterceptor에서 생성되어 Controller로 전달됩니다.
 */
@Getter
@RequiredArgsConstructor
public class DeviceInfo {
    private final String deviceId;
    private final String clientIp;
    private final String userAgent;
}
