# 6. 실제 사용 예시

## 질문

> **"이 방식으로 실제로 어떻게 사용할까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- Controller나 Orchestrator 메서드에 Annotation만 붙이면 되는 걸까?
- 어떤 파라미터에 @PermissionId를 붙여야 할까?
- 도메인별로 다른 Annotation을 만들어야 할까?

---

## 6.1 Lecture 도메인 예시 (USER 타입)

### 도메인 모델

```java
public class Lecture implements ResourceOwnership {
    private Long id;
    private String title;
    private String description;
    private Long createdBy;  // 생성한 유저 ID
    
    @Override
    public Long getOwnershipId() {
        return createdBy;
    }
}
```

**소유권 기준:** 생성한 유저 ID (`createdBy`)

### SearchService 구현

```java
@Service
@RequiredArgsConstructor
public class LectureSearchService implements DomainFinder<Lecture> {
    private final LectureService lectureService;
    
    @Override
    public Lecture searchById(Long id) {
        return lectureService.findById(id);
    }
}
```

### Annotation 정의

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = LectureSearchService.class,
    type = DataPermissionCheckType.USER
)
public @interface CheckLecturePermission {
}
```

**설명:**
- `finder`: `LectureSearchService`로 Lecture 도메인 조회
- `type`: `USER` - 사용자 ID로 검증

### Controller에서 사용

```java
@RestController
@RequiredArgsConstructor
public class LectureController implements LectureApi {
    private final LectureOrchestrator lectureOrchestrator;
    
    @Override
    @CheckLecturePermission
    public LectureResponse getLecture(@PermissionId Long lectureId) {
        return lectureOrchestrator.getLecture(lectureId);
    }
    
    @Override
    public LectureResponse createLecture(CreateLectureRequest request, UserInfo userInfo) {
        return lectureOrchestrator.createLecture(
            request.getTitle(),
            request.getDescription(),
            userInfo.getUserId()
        );
    }
}
```

**동작 과정:**

1. `getLecture(lectureId)` 호출
2. Aspect가 `@CheckLecturePermission` 감지
3. `@PermissionId`가 붙은 `lectureId` 추출
4. `LectureSearchService`로 Lecture 조회
5. Lecture의 `createdBy`와 현재 사용자 ID 비교
6. 일치하면 메서드 실행, 불일치하면 `AccessDeniedException`

---

## 6.2 Group 도메인 예시 (GROUP 타입)

### 도메인 모델

```java
public class Group implements ResourceOwnership {
    private Long id;
    private String name;
    private String description;
    
    @Override
    public Long getOwnershipId() {
        return id;  // 그룹 ID가 소유권 ID
    }
}
```

**소유권 기준:** 그룹 ID (`id`)

### SearchService 구현

```java
@Service
@RequiredArgsConstructor
public class GroupSearchService implements DomainFinder<Group> {
    private final GroupService groupService;
    
    @Override
    public Group searchById(Long id) {
        return groupService.findById(id);
    }
}
```

### Annotation 정의

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CheckDataPermission(
    finder = GroupSearchService.class,
    type = DataPermissionCheckType.GROUP
)
public @interface CheckGroupPermission {
}
```

**설명:**
- `finder`: `GroupSearchService`로 Group 도메인 조회
- `type`: `GROUP` - 그룹 ID로 검증

### Controller에서 사용

```java
@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApi {
    private final GroupOrchestrator groupOrchestrator;
    
    @Override
    @CheckGroupPermission
    public GroupResponse getGroup(@PermissionId Long groupId) {
        return groupOrchestrator.getGroup(groupId);
    }
    
    @Override
    public GroupResponse createGroup(CreateGroupRequest request) {
        // 그룹 생성 (테스트용)
        return groupOrchestrator.createGroup(request.getName(), request.getDescription());
    }
    
    @Override
    public void addUserToGroup(Long groupId, AddUserToGroupRequest request) {
        // 유저를 그룹에 매핑 (테스트용)
        groupOrchestrator.addUserToGroup(groupId, request.getUserId());
    }
}
```

**동작 과정:**

1. `getGroup(groupId)` 호출
2. Aspect가 `@CheckGroupPermission` 감지
3. `@PermissionId`가 붙은 `groupId` 추출
4. `GroupSearchService`로 Group 조회
5. Group의 `id`와 현재 사용자의 `groupId` 비교
6. 일치하면 메서드 실행, 불일치하면 `AccessDeniedException`

---

## 6.3 Orchestrator에서 사용

### Before (시도 1, 2)

```java
@Component
@RequiredArgsConstructor
public class LectureOrchestrator {
    private final LectureService lectureService;
    private final AuthenticateValidator authenticateValidator;
    
    public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
        Lecture lecture = lectureService.findById(lectureId);
        
        // 보안 로직 (비즈니스 로직과 혼재)
        authenticateValidator.validateByUser(lecture.getCreatedBy(), userInfo.getUserId());
        
        // 비즈니스 로직
        lecture.update(...);
        return lectureResponse;
    }
}
```

### After (AOP 적용)

```java
@Component
@RequiredArgsConstructor
public class LectureOrchestrator {
    private final LectureService lectureService;
    
    @CheckLecturePermission
    public LectureResponse updateLecture(@PermissionId Long lectureId) {
        // 순수 비즈니스 로직만
        Lecture lecture = lectureService.findById(lectureId);
        lecture.update(...);
        return lectureResponse;
    }
}
```

**차이점:**
- ✅ 보안 로직이 완전히 제거됨
- ✅ 비즈니스 로직만 남아 가독성 향상
- ✅ Annotation만 붙이면 자동으로 검증 수행

---

## 6.4 사용 가이드

### 1. Annotation 붙이기

**필수:**
- 메서드에 `@CheckLecturePermission` (또는 도메인별 Annotation) 붙이기
- 검증할 ID 파라미터에 `@PermissionId` 붙이기

**예시:**
```java
@CheckLecturePermission
public LectureResponse getLecture(@PermissionId Long lectureId) {
    // ...
}
```

### 2. 도메인별 Annotation 만들기 (선택사항)

**장점:**
- 더 간편하게 사용 가능
- 도메인별로 다른 설정을 명확하게 표현

**예시:**
```java
// Lecture용
@CheckDataPermission(
    finder = LectureSearchService.class,
    type = DataPermissionCheckType.USER
)
public @interface CheckLecturePermission {
}

// Group용
@CheckDataPermission(
    finder = GroupSearchService.class,
    type = DataPermissionCheckType.GROUP
)
public @interface CheckGroupPermission {
}
```

### 3. 직접 @CheckDataPermission 사용하기

도메인별 Annotation을 만들지 않고 직접 사용할 수도 있습니다.

```java
@CheckDataPermission(
    finder = LectureSearchService.class,
    type = DataPermissionCheckType.USER
)
public LectureResponse getLecture(@PermissionId Long lectureId) {
    // ...
}
```

---

## 6.5 주의사항

### 1. @PermissionId는 필수

`@PermissionId`가 없으면 `IllegalStateException` 발생

```java
// ❌ 잘못된 예시
@CheckLecturePermission
public LectureResponse getLecture(Long lectureId) {  // @PermissionId 없음
    // ...
}

// ✅ 올바른 예시
@CheckLecturePermission
public LectureResponse getLecture(@PermissionId Long lectureId) {
    // ...
}
```

### 2. ID는 Long 타입이어야 함

현재 구현은 Long 타입만 지원합니다.

```java
// ❌ 잘못된 예시
@CheckLecturePermission
public LectureResponse getLecture(@PermissionId String lectureId) {  // String은 안됨
    // ...
}

// ✅ 올바른 예시
@CheckLecturePermission
public LectureResponse getLecture(@PermissionId Long lectureId) {
    // ...
}
```

### 3. SearchService는 DomainFinder를 구현해야 함

```java
// ✅ 올바른 예시
@Service
public class LectureSearchService implements DomainFinder<Lecture> {
    @Override
    public Lecture searchById(Long id) {
        return lectureService.findById(id);
    }
}
```

---

## 정리

✅ **Annotation만 붙이면 자동으로 데이터 소유권 체크 수행**

✅ **도메인별 Annotation을 만들어 더 간편하게 사용 가능**

✅ **Orchestrator에서 보안 로직이 완전히 제거되어 비즈니스 로직만 남음**

✅ **@PermissionId로 검증할 ID를 명시**

다음 챕터에서 이 내용을 이력서에 어떻게 작성하는지 알아보겠습니다.
