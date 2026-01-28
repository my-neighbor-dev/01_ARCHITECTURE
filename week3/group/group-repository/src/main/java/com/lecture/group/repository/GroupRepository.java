package com.lecture.group.repository;

import com.lecture.group.domain.Group;

public interface GroupRepository {
    Group findById(Long id);
    Group save(Group group);
}
