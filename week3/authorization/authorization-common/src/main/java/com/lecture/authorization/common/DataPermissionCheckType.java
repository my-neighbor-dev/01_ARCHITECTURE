package com.lecture.authorization.common;

/**
 * 데이터 소유권 검증 타입
 * 
 * USER: 사용자 ID로 검증 (resource.getOwnershipId() == user.userId)
 * GROUP: 그룹 ID로 검증 (resource.getOwnershipId() == user.groupId)
 */
public enum DataPermissionCheckType {
    USER {
        @Override
        public void validate(ResourceOwnership resource, UserInfo user) {
            if (resource.getOwnershipId() == null || user.getUserId() == null) {
                throw new IllegalStateException("Resource ownership ID or user ID is null");
            }
            if (!resource.getOwnershipId().equals(user.getUserId())) {
                throw new AccessDeniedException("Access Denied: User does not own this resource");
            }
        }
    },
    GROUP {
        @Override
        public void validate(ResourceOwnership resource, UserInfo user) {
            if (resource.getOwnershipId() == null || user.getGroupId() == null) {
                throw new IllegalStateException("Resource ownership ID or user group ID is null");
            }
            if (!resource.getOwnershipId().equals(user.getGroupId())) {
                throw new AccessDeniedException("Access Denied: User does not belong to this group");
            }
        }
    };

    /**
     * 리소스의 소유권을 검증합니다.
     * 
     * @param resource 검증할 리소스
     * @param user 사용자 정보
     * @throws AccessDeniedException 소유권이 없을 경우
     */
    public abstract void validate(ResourceOwnership resource, UserInfo user);

    /**
     * Access Denied 예외
     */
    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
