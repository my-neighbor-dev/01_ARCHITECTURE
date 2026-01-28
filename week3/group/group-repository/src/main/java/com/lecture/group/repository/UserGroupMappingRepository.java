package com.lecture.group.repository;

import com.lecture.group.domain.UserGroupMapping;

import java.util.Optional;

public interface UserGroupMappingRepository {
    UserGroupMapping save(UserGroupMapping mapping);
    Optional<UserGroupMapping> findByUserId(Long userId);
}
