package com.lecture.authorization.annotation;

import com.lecture.authorization.common.DataPermissionCheckType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 유저 소유권 검증 Annotation
 * 
 * 유저 도메인에 대한 소유권 검증을 수행합니다.
 * USER 타입으로 검증하므로, 유저의 ID와 사용자 ID를 비교합니다.
 * 
 * 실제로는 Aspect에서 UserSearchService를 사용합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = com.lecture.user.service.UserSearchService.class,
    type = DataPermissionCheckType.USER
)
public @interface CheckUserPermission {
}
