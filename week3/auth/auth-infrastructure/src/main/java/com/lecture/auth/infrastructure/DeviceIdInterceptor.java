package com.lecture.auth.infrastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * DeviceIdInterceptor
 * 
 * 요청마다 device_id 쿠키를 추출하거나 생성합니다.
 * DeviceInfo를 생성하여 Request에 저장합니다.
 */
@Component
public class DeviceIdInterceptor implements HandlerInterceptor {
    
    private static final String DEVICE_ID_COOKIE_NAME = "device_id";
    private static final int COOKIE_MAX_AGE = 365 * 24 * 60 * 60; // 1년
    public static final String DEVICE_INFO_ATTRIBUTE_NAME = "deviceInfo";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 쿠키에서 device_id 추출
        String deviceId = getOrCreateDeviceId(request, response);
        
        // DeviceInfo 생성
        DeviceInfo deviceInfo = new DeviceInfo(
            deviceId,
            extractClientIp(request),
            request.getHeader("User-Agent")
        );
        
        // Request에 저장
        request.setAttribute(DEVICE_INFO_ATTRIBUTE_NAME, deviceInfo);
        return true;
    }
    
    private String getOrCreateDeviceId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (DEVICE_ID_COOKIE_NAME.equals(cookie.getName())) {
                    String deviceId = cookie.getValue();
                    if (deviceId != null && !deviceId.isEmpty()) {
                        // 쿠키 갱신 (만료 시간 연장)
                        setDeviceIdCookie(response, deviceId);
                        return deviceId;
                    }
                }
            }
        }
        
        // device_id가 없으면 새로 생성
        String newDeviceId = UUID.randomUUID().toString();
        setDeviceIdCookie(response, newDeviceId);
        return newDeviceId;
    }
    
    private void setDeviceIdCookie(HttpServletResponse response, String deviceId) {
        Cookie cookie = new Cookie(DEVICE_ID_COOKIE_NAME, deviceId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }
    
    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
