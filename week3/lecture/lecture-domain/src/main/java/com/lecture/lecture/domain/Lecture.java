package com.lecture.lecture.domain;

import com.lecture.authorization.common.ResourceOwnership;
import lombok.Getter;

/**
 * 강의 도메인 모델
 * 
 * ResourceOwnership을 구현하여 소유권 검증에 사용됩니다.
 * USER 타입으로 검증하므로, createdBy 필드가 소유권 ID입니다.
 */
@Getter
public class Lecture implements ResourceOwnership {
    private Long id;
    private String title;
    private String description;
    private Long createdBy;  // 강의를 생성한 사용자 ID (소유권 ID)
    
    public Lecture(Long id, String title, String description, Long createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
    }
    
    @Override
    public Long getOwnershipId() {
        return createdBy;
    }
}
