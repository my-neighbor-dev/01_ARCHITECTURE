package com.lecture.authorization.common;

/**
 * 소유권을 가진 리소스가 구현해야 하는 인터페이스
 * 
 * 각 도메인 모델은 이 인터페이스를 구현하여 소유권 ID를 반환합니다.
 */
public interface ResourceOwnership {
    /**
     * 소유권을 식별하는 ID를 반환합니다.
     * 
     * @return 소유권 ID (사용자 ID 또는 그룹 ID 등)
     */
    Long getOwnershipId();
}
