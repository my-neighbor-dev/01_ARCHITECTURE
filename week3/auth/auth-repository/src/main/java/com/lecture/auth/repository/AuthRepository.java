package com.lecture.auth.repository;

import com.lecture.auth.domain.AuthToken;
import java.util.Optional;

public interface AuthRepository {
    void saveRefreshToken(AuthToken refreshToken);
    void saveAccessToken(AuthToken accessToken);
    void deleteRefreshTokenByUserId(Long userId);
    void deleteAccessTokenByUserId(Long userId);
    Optional<Long> findUserIdByToken(String token);
}
