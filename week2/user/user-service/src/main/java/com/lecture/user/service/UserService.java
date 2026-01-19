package com.lecture.user.service;

import com.lecture.user.repository.UserRepository;
import com.lecture.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * User Service 구현체
 * 
 * User 도메인 관련 비즈니스 로직을 처리합니다.
 *
 * 장점:
 * 1. Repository 인터페이스만 참조하여 구현체 교체 가능
 * 2. 단일 책임: 유저 조회만 담당
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * 유저 ID로 유저 정보 조회
     */
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 이메일로 유저 정보 조회
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 유저 생성 (테스트용)
     */
    public User createUser(String email, String name, String password, String phoneNumber) {
        User user = new User(null, email, name, password, phoneNumber);
        return userRepository.save(user);
    }
}
