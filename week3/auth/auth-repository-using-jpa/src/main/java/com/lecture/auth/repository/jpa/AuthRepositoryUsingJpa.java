package com.lecture.auth.repository.jpa;

import com.lecture.auth.domain.AuthToken;
import com.lecture.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryUsingJpa implements AuthRepository {
    
    private final AuthTokenJpaRepository authTokenJpaRepository;

    @Override
    public void saveRefreshToken(AuthToken refreshToken) {
        AuthTokenEntity entity = new AuthTokenEntity(
            refreshToken.getUserId(),
            refreshToken.getToken(),
            refreshToken.getExpiresAt()
        );
        authTokenJpaRepository.save(entity);
    }

    @Override
    public void saveAccessToken(AuthToken accessToken) {
        AuthTokenEntity entity = new AuthTokenEntity(
            accessToken.getUserId(),
            accessToken.getToken(),
            accessToken.getExpiresAt()
        );
        authTokenJpaRepository.save(entity);
    }

    @Override
    public void deleteRefreshTokenByUserId(Long userId) {
        AuthTokenEntity entity = authTokenJpaRepository.findByUserId(userId);
        if (entity != null) {
            authTokenJpaRepository.delete(entity);
        }
    }

    @Override
    public void deleteAccessTokenByUserId(Long userId) {
        // Access Token도 동일한 방식으로 삭제
        deleteRefreshTokenByUserId(userId);
    }

    @Override
    public Optional<Long> findUserIdByToken(String token) {
        return authTokenJpaRepository.findByToken(token)
            .filter(entity -> entity.getExpiresAt() > System.currentTimeMillis())
            .map(AuthTokenEntity::getUserId);
    }
}
