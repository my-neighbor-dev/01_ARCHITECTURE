package com.lecture.authorization.annotation;

import com.lecture.authorization.common.DataPermissionCheckType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 그룹 소유권 검증 Annotation
 * 
 * 그룹 도메인에 대한 소유권 검증을 수행합니다.
 * GROUP 타입으로 검증하므로, 그룹의 ID와 사용자의 groupId를 비교합니다.
 * 
 * 실제로는 Aspect에서 GroupSearchService를 사용합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = com.lecture.group.service.GroupSearchService.class,
    type = DataPermissionCheckType.GROUP
)
public @interface CheckGroupPermission {
}
