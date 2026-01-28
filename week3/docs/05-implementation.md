# 5. 구현 단계별 설명

## 질문

> **"AOP에서 도메인 객체를 어떻게 조회하고 검증할까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- Aspect에서 어떤 도메인을 조회해야 하는지 어떻게 알 수 있을까?
- Annotation에 도메인 정보를 어떻게 전달할까?
- 검증할 ID를 파라미터에서 어떻게 추출할까?

---

## 5.1 Step 1: 핵심 인터페이스 정의

### ResourceOwnership 인터페이스

먼저, 소유권을 가진 모든 도메인 모델이 공통으로 구현해야 할 인터페이스를 정의했습니다.

**이유:**
- 검증에 해당하는 ID 비교 로직은 동일하게 사용할 수 있지만
- 도메인별로 어떤 ID를 검증에 사용해야 하는지가 달랐기 때문

**인터페이스 정의:**

```java
// 소유권 식별자를 반환하는 인터페이스
public interface ResourceOwnership {
    Long getOwnershipId();
}
```

**도메인 구현 예시:**

```java
// Lecture.java - 생성자 ID 반환
public class Lecture implements ResourceOwnership {
    private Long id;
    private Long createdBy; // 생성한 유저 ID
    // ...
    
    @Override
    public Long getOwnershipId() {
        return createdBy;
    }
}

// Group.java - 그룹 ID 반환
public class Group implements ResourceOwnership {
    private Long id;
    // ...
    
    @Override
    public Long getOwnershipId() {
        return id;  // 그룹 ID가 소유권 ID
    }
}
```

**핵심:**
- 각 도메인은 자신의 소유권 기준에 맞는 ID를 반환
- 공통 인터페이스로 통일된 검증 로직 사용 가능

---

## 5.2 Step 2: 전략 패턴으로 검증 로직 추상화

### DataPermissionCheckType Enum

검증 기준이 "사용자 본인"이냐 "소속 그룹"이냐에 따라 다르므로, 이를 Enum 전략 패턴으로 분리했습니다.

**Enum 정의:**

```java
public enum DataPermissionCheckType {
    USER {
        @Override
        public void validate(ResourceOwnership resource, UserInfo user) {
            if (!resource.getOwnershipId().equals(user.getUserId())) {
                throw new AccessDeniedException("Access Denied: User does not own this resource");
            }
        }
    },
    GROUP {
        @Override
        public void validate(ResourceOwnership resource, UserInfo user) {
            if (!resource.getOwnershipId().equals(user.getGroupId())) {
                throw new AccessDeniedException("Access Denied: User does not belong to this group");
            }
        }
    };

    public abstract void validate(ResourceOwnership resource, UserInfo user);
}
```

**설명:**
- `USER`: 리소스의 소유권 ID와 사용자 ID를 비교
- `GROUP`: 리소스의 소유권 ID와 사용자 그룹 ID를 비교

**왜 Enum을 사용했나?**
- 검증 로직이 간단했기 때문에 별도의 클래스로 분리할 필요가 없었음
- Enum으로 깔끔하게 구현 가능

---

## 5.3 Step 3: 도메인 조회를 위한 DomainFinder 인터페이스 정의

### DomainFinder 인터페이스

Aspect에서 검증할 객체를 조회하기 위해, 기존 SearchService들이 구현할 공통 인터페이스를 정의합니다.

**인터페이스 정의:**

```java
// 리소스를 ID로 조회할 수 있는 '검색기' 인터페이스
public interface DomainFinder<T extends ResourceOwnership> {
    T searchById(Long id);
}
```

**기존 SearchService 구현:**

```java
@Service
public class LectureSearchService implements DomainFinder<Lecture> {
    private final LectureService lectureService;
    
    @Override
    public Lecture searchById(Long id) {
        return lectureService.findById(id);
    }
}

@Service
public class GroupSearchService implements DomainFinder<Group> {
    private final GroupService groupService;
    
    @Override
    public Group searchById(Long id) {
        return groupService.findById(id);
    }
}
```

**핵심:**
- 기존 SearchService에 인터페이스만 구현하면 됨
- Aspect에서 어떤 도메인을 조회할지 Annotation으로 지정 가능

---

## 5.4 Step 4: Annotation & Aspect 구현

### Annotation 정의

개발자가 "이 메서드는 검사가 필요해!"라고 깃발을 꽂을 수 있는 애너테이션을 만듭니다.

**CheckDataPermission Annotation:**

```java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckDataPermission {
    Class<? extends DomainFinder<?>> finder(); // 누가 조회할 것인가?
    DataPermissionCheckType type();            // 어떤 기준으로 검사할 것인가?
}
```

**PermissionId Annotation:**

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionId {
}
```

**설명:**
- `finder`: 어떤 SearchService로 도메인을 조회할지 지정
- `type`: 어떤 기준으로 검증할지 지정 (USER or GROUP)
- `@PermissionId`: 검증할 ID가 담긴 파라미터에 붙임

### Aspect 구현

Annotation이 붙은 메서드를 가로채서 처리할 Aspect를 만듭니다.

**DataPermissionAspect:**

```java
@Aspect
@Component
public class DataPermissionAspect {
    private final ApplicationContext applicationContext;
    
    @Around("@annotation(com.lecture.authorization.annotation.CheckDataPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 사용자 정보 획득 (Request에서 가져오기)
        UserInfo userInfo = extractUserInfo();
        
        // 2. Annotation 정보 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CheckDataPermission checkDataPermission = AnnotationUtils.findAnnotation(
            method, 
            CheckDataPermission.class
        );
        
        // 3. ID 파라미터 추출 (@PermissionId가 붙은 Long 타입 찾기)
        Long id = extractPermissionId(joinPoint, method);
        
        // 4. Finder Bean으로 리소스 조회
        DomainFinder<?> finder = applicationContext.getBean(checkDataPermission.finder());
        ResourceOwnership resource = finder.searchById(id);
        
        // 5. 검증 수행
        checkDataPermission.type().validate(resource, userInfo);
        
        // 6. 검증 통과 시 원래 메서드 실행
        return joinPoint.proceed();
    }
}
```

**단계별 설명:**

1. **사용자 정보 획득**: Request에서 UserInfo 가져오기 (UserInfoInterceptor에서 설정)
2. **Annotation 정보 추출**: 메서드에 붙은 `@CheckDataPermission` 정보 가져오기
3. **ID 파라미터 추출**: `@PermissionId`가 붙은 파라미터에서 ID 추출
4. **리소스 조회**: Annotation의 `finder`로 도메인 객체 조회
5. **검증 수행**: Annotation의 `type`으로 검증 수행
6. **메서드 실행**: 검증 통과 시 원래 메서드 실행

---

## 5.5 실제 사용 예시

### 도메인별 Annotation 생성 (선택사항)

더 간편하게 사용하기 위해 도메인별 Annotation을 만들 수 있습니다.

```java
// Lecture용 Annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = LectureSearchService.class,
    type = DataPermissionCheckType.USER
)
public @interface CheckLecturePermission {
}

// Group용 Annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = GroupSearchService.class,
    type = DataPermissionCheckType.GROUP
)
public @interface CheckGroupPermission {
}
```

**사용:**

```java
@CheckLecturePermission
public LectureResponse updateLecture(@PermissionId Long lectureId) {
    // 순수 비즈니스 로직
    return lectureOrchestrator.updateLecture(lectureId);
}
```

---

## 정리

✅ **ResourceOwnership 인터페이스로 도메인별 소유권 ID 통일**

✅ **전략 패턴(Enum)으로 검증 로직 추상화**

✅ **DomainFinder 인터페이스로 도메인 조회 추상화**

✅ **AOP로 비즈니스 로직과 보안 로직 분리**

✅ **Annotation 기반으로 간편하게 사용 가능**

다음 챕터에서 실제 사용 예시를 더 자세히 알아보겠습니다.
