package com.lecture.auth.service;

import com.lecture.auth.infrastructure.DeviceInfo;
import com.lecture.auth.repository.RateLimitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * RateLimitingService
 * 
 * Rate Limiting 로직을 처리하는 Service입니다.
 * 전화번호, Device ID, IP 기반으로 다중 체크를 수행합니다.
 */
@Service
@RequiredArgsConstructor
public class RateLimitingService {
    
    private final RateLimitingRepository rateLimitingRepository;
    
    private static final long MAX_REQUESTS_PER_HOUR_BY_PHONE = 5L;
    private static final long MAX_REQUESTS_PER_HOUR_BY_DEVICE = 5L;
    private static final long MAX_REQUESTS_PER_HOUR_BY_IP = 100L;
    private static final long RATE_LIMIT_WINDOW_SECONDS = 60 * 60L; // 1시간
    
    private static final String PHONE_RATE_LIMIT_KEY_PREFIX = "rate_limit:login:phone:";
    private static final String DEVICE_RATE_LIMIT_KEY_PREFIX = "rate_limit:login:device:";
    private static final String IP_RATE_LIMIT_KEY_PREFIX = "rate_limit:login:ip:";
    
    /**
     * Rate Limit 체크
     * 전화번호, Device ID, IP를 모두 체크합니다.
     */
    public void checkRateLimit(String phoneNumber, DeviceInfo deviceInfo) {
        checkPhoneRateLimit(phoneNumber);
        checkDeviceRateLimit(deviceInfo);
    }
    
    /**
     * 전화번호 기반 Rate Limit 체크
     */
    public void checkPhoneRateLimit(String phoneNumber) {
        String key = PHONE_RATE_LIMIT_KEY_PREFIX + phoneNumber;
        Long count = rateLimitingRepository.incrementAndGet(key, RATE_LIMIT_WINDOW_SECONDS);
        
        if (count > MAX_REQUESTS_PER_HOUR_BY_PHONE) {
            throw new RateLimitExceededException(
                "호출할 수 있는 인증 횟수를 초과하였습니다. 1시간 후 다시 시도해주세요."
            );
        }
    }
    
    /**
     * Device ID 기반 Rate Limit 체크
     */
    public void checkDeviceRateLimit(DeviceInfo deviceInfo) {
        // Device ID 체크
        String deviceKey = DEVICE_RATE_LIMIT_KEY_PREFIX + deviceInfo.getDeviceId();
        Long deviceCount = rateLimitingRepository.incrementAndGet(
            deviceKey, 
            RATE_LIMIT_WINDOW_SECONDS
        );
        
        if (deviceCount > MAX_REQUESTS_PER_HOUR_BY_DEVICE) {
            throw new RateLimitExceededException(
                "호출할 수 있는 인증 횟수를 초과하였습니다. 1시간 후 다시 시도해주세요."
            );
        }
        
        // IP 체크 (IP만 사용)
        String ipKey = IP_RATE_LIMIT_KEY_PREFIX + deviceInfo.getClientIp();
        Long ipCount = rateLimitingRepository.incrementAndGet(
            ipKey, 
            RATE_LIMIT_WINDOW_SECONDS
        );
        
        if (ipCount > MAX_REQUESTS_PER_HOUR_BY_IP) {
            throw new RateLimitExceededException(
                "호출할 수 있는 인증 횟수를 초과하였습니다. 1시간 후 다시 시도해주세요."
            );
        }
    }
    
    /**
     * Rate Limit 리셋 (로그인 성공 시 모든 Rate Limit 카운트 삭제)
     */
    public void resetRateLimit(String phoneNumber, DeviceInfo deviceInfo) {
        // 전화번호 기반 Rate Limit 리셋
        String phoneKey = PHONE_RATE_LIMIT_KEY_PREFIX + phoneNumber;
        rateLimitingRepository.delete(phoneKey);
        
        // Device ID 기반 Rate Limit 리셋
        String deviceKey = DEVICE_RATE_LIMIT_KEY_PREFIX + deviceInfo.getDeviceId();
        rateLimitingRepository.delete(deviceKey);
    }

    /**
     * Rate Limit 초과 예외
     */
    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }
}
