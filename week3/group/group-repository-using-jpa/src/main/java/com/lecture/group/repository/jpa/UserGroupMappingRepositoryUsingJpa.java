package com.lecture.group.repository.jpa;

import com.lecture.group.domain.UserGroupMapping;
import com.lecture.group.repository.UserGroupMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserGroupMappingRepositoryUsingJpa implements UserGroupMappingRepository {
    
    private final UserGroupMappingJpaRepository jpaRepository;
    
    @Override
    public UserGroupMapping save(UserGroupMapping mapping) {
        UserGroupMappingEntity entity = convertToEntity(mapping);
        UserGroupMappingEntity saved = jpaRepository.save(entity);
        return convertToDomain(saved);
    }
    
    @Override
    public Optional<UserGroupMapping> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
            .map(this::convertToDomain);
    }
    
    private UserGroupMappingEntity convertToEntity(UserGroupMapping mapping) {
        if (mapping.getId() == null) {
            return new UserGroupMappingEntity(mapping.getUserId(), mapping.getGroupId());
        }
        UserGroupMappingEntity entity = new UserGroupMappingEntity(mapping.getUserId(), mapping.getGroupId());
        entity.setId(mapping.getId());
        return entity;
    }
    
    private UserGroupMapping convertToDomain(UserGroupMappingEntity entity) {
        return new UserGroupMapping(entity.getId(), entity.getUserId(), entity.getGroupId());
    }
}
