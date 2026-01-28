package com.lecture.auth.repository.jpa;

import com.lecture.auth.domain.AuthToken;
import com.lecture.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public void deleteRefreshTokenByUserId(Long userId) {
        AuthTokenEntity entity = authTokenJpaRepository.findByUserId(userId);
        if (entity != null) {
            authTokenJpaRepository.delete(entity);
        }
    }
}
