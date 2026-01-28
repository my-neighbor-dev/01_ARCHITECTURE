package com.lecture.group.service;

import com.lecture.group.domain.Group;
import com.lecture.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {
    
    private final GroupRepository groupRepository;
    
    public Group findById(Long id) {
        return groupRepository.findById(id);
    }
    
    public Group create(String name, String description) {
        Group group = new Group(null, name, description);
        return groupRepository.save(group);
    }
}
