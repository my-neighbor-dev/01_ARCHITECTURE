package com.lecture.group.repository.jpa;

import com.lecture.group.domain.Group;
import com.lecture.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryUsingJpa implements GroupRepository {
    
    private final GroupJpaRepository jpaRepository;
    
    @Override
    public Group findById(Long id) {
        GroupEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Group not found: " + id));
        return convertToDomain(entity);
    }
    
    @Override
    public Group save(Group group) {
        GroupEntity entity = convertToEntity(group);
        GroupEntity saved = jpaRepository.save(entity);
        return convertToDomain(saved);
    }
    
    private GroupEntity convertToEntity(Group group) {
        if (group.getId() == null) {
            return new GroupEntity(group.getName(), group.getDescription());
        }
        GroupEntity entity = new GroupEntity();
        entity.setId(group.getId());
        entity.setName(group.getName());
        entity.setDescription(group.getDescription());
        return entity;
    }
    
    private Group convertToDomain(GroupEntity entity) {
        return new Group(entity.getId(), entity.getName(), entity.getDescription());
    }
}
