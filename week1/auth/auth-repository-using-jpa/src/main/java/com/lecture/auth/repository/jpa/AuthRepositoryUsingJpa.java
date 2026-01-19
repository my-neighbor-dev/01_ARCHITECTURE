package com.lecture.auth.repository.jpa;

import com.lecture.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryUsingJpa implements AuthRepository {
    
    private final AuthTokenJpaRepository authTokenJpaRepository;
    
    @Override
    public void saveToken(Long userId, String token, Long expiresAt) {
        AuthTokenEntity entity = new AuthTokenEntity(
            userId,
            token,
            expiresAt
        );
        authTokenJpaRepository.save(entity);
    }
    
    @Override
    public String findTokenByUserId(Long userId) {
        AuthTokenEntity entity = authTokenJpaRepository.findByUserId(userId);
        if (entity == null) {
            return null;
        }
        
        return entity.getToken();
    }
}
