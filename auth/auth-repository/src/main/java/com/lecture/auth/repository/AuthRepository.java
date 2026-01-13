package com.lecture.auth.repository;

/**
 * AFTER 코드: AuthRepository 인터페이스
 */
public interface AuthRepository {
    void saveToken(Long userId, String token, Long expiresAt);
    String findTokenByUserId(Long userId);
}
