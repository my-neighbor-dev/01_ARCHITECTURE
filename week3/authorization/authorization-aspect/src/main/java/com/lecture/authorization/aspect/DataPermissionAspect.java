package com.lecture.authorization.aspect;

import com.lecture.authorization.annotation.CheckDataPermission;
import com.lecture.authorization.annotation.CheckGroupPermission;
import com.lecture.authorization.annotation.CheckLecturePermission;
import com.lecture.authorization.annotation.CheckUserPermission;
import com.lecture.authorization.annotation.PermissionId;
import com.lecture.authorization.common.DataPermissionCheckType;
import com.lecture.authorization.common.DomainFinder;
import com.lecture.authorization.common.ResourceOwnership;
import com.lecture.authorization.common.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 데이터 소유권 검증을 수행하는 Aspect
 * 
 * @CheckDataPermission 또는 이를 메타 어노테이션으로 가진 어노테이션(@CheckUserPermission, @CheckLecturePermission, @CheckGroupPermission)이
 * 붙은 메서드를 가로채서 데이터 소유권 검증을 수행합니다.
 */
@Aspect
@Component
public class DataPermissionAspect {
    
    private final ApplicationContext applicationContext;
    
    public DataPermissionAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Around("@annotation(com.lecture.authorization.annotation.CheckDataPermission) || " +
            "@annotation(com.lecture.authorization.annotation.CheckUserPermission) || " +
            "@annotation(com.lecture.authorization.annotation.CheckLecturePermission) || " +
            "@annotation(com.lecture.authorization.annotation.CheckGroupPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 메서드 정보 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 2. CheckDataPermission Annotation 추출
        CheckDataPermission checkDataPermission = AnnotationUtils.findAnnotation(
            method, 
            CheckDataPermission.class
        );
        if (checkDataPermission == null) {
            throw new IllegalStateException("@CheckDataPermission annotation not found");
        }
        
        // 3. UserInfo 추출 (Request에서 가져오기)
        UserInfo userInfo = extractUserInfo();
        if (userInfo == null) {
            throw new IllegalStateException("UserInfo not found in request. UserInfoInterceptor must be registered.");
        }
        
        // 4. @PermissionId가 붙은 파라미터에서 ID 추출
        Long id = extractPermissionId(joinPoint, method);
        if (id == null) {
            throw new IllegalStateException("@PermissionId로 지정된 Long 파라미터가 없습니다.");
        }
        
        // 5. DomainFinder Bean으로 리소스 조회
        DomainFinder<?> finder = applicationContext.getBean(checkDataPermission.finder());
        ResourceOwnership resource = finder.searchById(id);
        
        // 6. 검증 수행
        DataPermissionCheckType type = checkDataPermission.type();
        type.validate(resource, userInfo);
        
        // 7. 검증 통과 시 원래 메서드 실행
        return joinPoint.proceed();
    }
    
    /**
     * UserInfo를 Request에서 추출합니다.
     * UserInfoInterceptor에서 Request에 저장한 값을 가져옵니다.
     */
    private UserInfo extractUserInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return (UserInfo) request.getAttribute(UserInfoArgumentResolver.USER_INFO_ATTRIBUTE_NAME);
    }
    
    /**
     * @PermissionId가 붙은 파라미터에서 ID를 추출합니다.
     */
    private Long extractPermissionId(ProceedingJoinPoint joinPoint, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PermissionId.class)) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                }
            }
        }
        return null;
    }
}
