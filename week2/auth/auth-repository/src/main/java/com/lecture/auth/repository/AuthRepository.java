package com.lecture.auth.repository;

import com.lecture.auth.domain.AuthToken;

public interface AuthRepository {
    void saveRefreshToken(AuthToken refreshToken);
    void deleteRefreshTokenByUserId(Long userId);
}
