package com.lecture.group.orchestrator;

import com.lecture.group.api.GroupResponse;
import com.lecture.group.domain.Group;
import com.lecture.group.service.GroupService;
import com.lecture.group.service.UserGroupMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupOrchestrator {
    
    private final GroupService groupService;
    private final UserGroupMappingService userGroupMappingService;
    
    public GroupResponse getGroup(Long groupId) {
        Group group = groupService.findById(groupId);
        return new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription()
        );
    }
    
    public GroupResponse createGroup(String name, String description) {
        Group group = groupService.create(name, description);
        return new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription()
        );
    }
    
    public void addUserToGroup(Long groupId, Long userId) {
        userGroupMappingService.addUserToGroup(userId, groupId);
    }
}
