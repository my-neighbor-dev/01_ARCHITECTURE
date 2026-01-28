package com.lecture.group.service;

import com.lecture.group.domain.UserGroupMapping;
import com.lecture.group.repository.UserGroupMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGroupMappingService {
    
    private final UserGroupMappingRepository userGroupMappingRepository;
    
    public UserGroupMapping addUserToGroup(Long userId, Long groupId) {
        UserGroupMapping mapping = new UserGroupMapping(null, userId, groupId);
        return userGroupMappingRepository.save(mapping);
    }
    
    public java.util.Optional<UserGroupMapping> findByUserId(Long userId) {
        return userGroupMappingRepository.findByUserId(userId);
    }
}
