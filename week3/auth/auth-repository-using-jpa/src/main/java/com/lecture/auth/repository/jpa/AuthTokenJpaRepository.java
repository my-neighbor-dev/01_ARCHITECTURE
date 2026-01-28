package com.lecture.auth.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthTokenJpaRepository extends JpaRepository<AuthTokenEntity, Long> {
    AuthTokenEntity findByUserId(Long userId);
    Optional<AuthTokenEntity> findByToken(String token);
}
