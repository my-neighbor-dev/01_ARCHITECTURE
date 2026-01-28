package com.lecture.authorization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 검증할 리소스의 ID를 지정하는 Annotation
 * 
 * 메서드 파라미터에 이 Annotation을 붙이면,
 * Aspect에서 해당 파라미터 값을 추출하여 소유권 검증에 사용합니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionId {
}
