package com.lecture.user.repository;

import com.lecture.user.domain.User;

/**
 * AFTER 코드: Repository 인터페이스
 * 
 * 장점:
 * 1. 구현체를 유연하게 교체 가능 (JPA → JDBC 등)
 * 2. Service는 인터페이스만 의존
 * 3. 테스트 시 Mock 객체 주입 용이
 */
public interface UserRepository {
    User findById(Long id);
    User findByEmail(String email);
}
