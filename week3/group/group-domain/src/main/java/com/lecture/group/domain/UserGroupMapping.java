package com.lecture.group.domain;

import lombok.Getter;

/**
 * 사용자-그룹 매핑 도메인 모델
 * 
 * 사용자가 어떤 그룹에 속해있는지를 나타냅니다.
 */
@Getter
public class UserGroupMapping {
    private Long id;
    private Long userId;
    private Long groupId;
    
    public UserGroupMapping(Long id, Long userId, Long groupId) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
    }
}
