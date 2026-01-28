package com.lecture.user.service;

import com.lecture.authorization.common.DomainFinder;
import com.lecture.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 유저 조회를 위한 SearchService
 * 
 * DomainFinder를 구현하여 Aspect에서 사용됩니다.
 */
@Service
@RequiredArgsConstructor
public class UserSearchService implements DomainFinder<User> {
    
    private final UserService userService;
    
    @Override
    public User searchById(Long id) {
        return userService.getUserById(id);
    }
}
