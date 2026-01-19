package com.lecture.auth.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenJpaRepository extends JpaRepository<AuthTokenEntity, Long> {
    AuthTokenEntity findByUserId(Long userId);
}
