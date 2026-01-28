package com.lecture.group.controller;

import com.lecture.authorization.annotation.CheckGroupPermission;
import com.lecture.authorization.annotation.PermissionId;
import com.lecture.group.api.AddUserToGroupRequest;
import com.lecture.group.api.CreateGroupRequest;
import com.lecture.group.api.GroupApi;
import com.lecture.group.api.GroupResponse;
import com.lecture.group.orchestrator.GroupOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Group API 구현체
 * 
 * @CheckGroupPermission Annotation이 붙은 메서드는
 * Aspect에 의해 자동으로 소유권 검증이 수행됩니다.
 */
@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApi {
    
    private final GroupOrchestrator groupOrchestrator;
    
    @Override
    @CheckGroupPermission
    public GroupResponse getGroup(@PermissionId Long groupId) {
        // Aspect가 자동으로 소유권 검증을 수행합니다.
        // 자신이 속한 그룹만 조회 가능합니다.
        return groupOrchestrator.getGroup(groupId);
    }
    
    @Override
    public GroupResponse createGroup(CreateGroupRequest request) {
        // 그룹 생성 (테스트용)
        return groupOrchestrator.createGroup(request.getName(), request.getDescription());
    }
    
    @Override
    public void addUserToGroup(Long groupId, AddUserToGroupRequest request) {
        // 유저를 그룹에 매핑 (테스트용)
        groupOrchestrator.addUserToGroup(groupId, request.getUserId());
    }
}
