package com.lecture.authorization.annotation;

import com.lecture.authorization.common.DataPermissionCheckType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 강의 소유권 검증 Annotation
 * 
 * 강의 도메인에 대한 소유권 검증을 수행합니다.
 * USER 타입으로 검증하므로, 강의의 createdBy와 사용자 ID를 비교합니다.
 * 
 * 실제로는 Aspect에서 LectureSearchService를 사용합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = com.lecture.lecture.service.LectureSearchService.class,
    type = DataPermissionCheckType.USER
)
public @interface CheckLecturePermission {
}
