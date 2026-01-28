package com.lecture.user.repository.jpa;

import com.lecture.user.repository.UserRepository;
import com.lecture.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * AFTER 코드: JPA 구현체
 * 
 * Repository 인터페이스를 구현하여 JPA를 사용하는 구현체
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryUsingJpa implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    
    @Override
    public User findById(Long id) {
        UserEntity entity = userJpaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToUser(entity);
    }
    
    @Override
    public User findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email);
        if (entity == null) {
            throw new RuntimeException("User not found");
        }
        
        return convertToUser(entity);
    }
    
    @Override
    public User save(User user) {
        UserEntity entity = convertToEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return convertToUser(savedEntity);
    }
    
    private UserEntity convertToEntity(User user) {
        // 생성자 사용 (setter 없이)
        return new UserEntity(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPassword(),
            user.getPhoneNumber()
        );
    }
    
    private User convertToUser(UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getName(),
            entity.getPassword(),
            entity.getPhoneNumber()
        );
    }
}
