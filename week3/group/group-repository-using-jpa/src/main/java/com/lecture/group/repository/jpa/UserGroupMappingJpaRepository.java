package com.lecture.group.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupMappingJpaRepository extends JpaRepository<UserGroupMappingEntity, Long> {
    Optional<UserGroupMappingEntity> findByUserId(Long userId);
}
