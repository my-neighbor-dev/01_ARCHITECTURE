package com.lecture.auth.external.api.feign;

import com.lecture.auth.domain.AuthUser;
import com.lecture.auth.external.api.AuthUserApi;
import com.lecture.user.api.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * AuthUserApi의 Feign 구현체
 * 
 * Auth 모듈에서 User 모듈의 API를 호출할 때 사용합니다.
 * 
 * week2: name 필드 추가
 */
@Component
@RequiredArgsConstructor
public class AuthUserApiFeign implements AuthUserApi {
    
    private final UserApiFeignClient userApiFeignClient;
    
    @Override
    public AuthUser getUserByEmail(String email) {
        AuthUserResponse authUserResponse = userApiFeignClient.getUserByEmail(email);
        return toAuthUser(authUserResponse);
    }
    
    /**
     * AuthUserResponse DTO를 AuthUser Domain 객체로 변환
     */
    private AuthUser toAuthUser(AuthUserResponse authUserResponse) {
        if (authUserResponse == null) {
            return null;
        }
        return new AuthUser(
            authUserResponse.getId(),
            authUserResponse.getEmail(),
            authUserResponse.getName(),  // week2: name 필드 추가
            authUserResponse.getPassword(),
            authUserResponse.getPhoneNumber()  // week2: phoneNumber 필드 추가
        );
    }

    @FeignClient(name = "lecture-service", url = "${lecture.service.url:http://localhost:8080}")
    public interface UserApiFeignClient {
        @GetMapping("/api/users/by-email/{email}")
        AuthUserResponse getUserByEmail(@PathVariable("email") String email);
    }
}
