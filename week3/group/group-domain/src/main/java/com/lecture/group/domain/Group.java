package com.lecture.group.domain;

import com.lecture.authorization.common.ResourceOwnership;
import lombok.Getter;

/**
 * 그룹 도메인 모델
 * 
 * ResourceOwnership을 구현하여 소유권 검증에 사용됩니다.
 * GROUP 타입으로 검증하므로, id 필드가 소유권 ID입니다.
 */
@Getter
public class Group implements ResourceOwnership {
    private Long id;
    private String name;
    private String description;
    
    public Group(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public Long getOwnershipId() {
        return id;  // 그룹 ID가 소유권 ID
    }
}
