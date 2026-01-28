package com.lecture.authorization.annotation;

import com.lecture.authorization.common.DataPermissionCheckType;
import com.lecture.authorization.common.DomainFinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 데이터 소유권 검증을 수행하는 Annotation
 * 
 * 이 Annotation이 붙은 메서드는 Aspect에 의해 가로채져서
 * 데이터 소유권 검증이 수행됩니다.
 * 
 * @param finder 도메인을 조회할 DomainFinder 클래스
 * @param type 검증 타입 (USER 또는 GROUP)
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckDataPermission {
    /**
     * 도메인을 조회할 DomainFinder 클래스
     */
    Class<? extends DomainFinder<?>> finder();
    
    /**
     * 검증 타입 (USER 또는 GROUP)
     */
    DataPermissionCheckType type();
}
