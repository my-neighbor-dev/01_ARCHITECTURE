# 4. AOP + 전략 패턴으로 해결

## 질문

> **"비즈니스 로직을 건드리지 않고 보안 로직을 추가할 수 있을까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- AOP(Aspect-Oriented Programming)가 무엇일까?
- 메서드 실행 전에 자동으로 권한 체크를 할 수 있을까?
- Annotation만 붙이면 자동으로 검증이 수행되도록 할 수 있을까?

---

## 4.1 최종 결정: AOP + 전략 패턴

### 목표

**"기존 비즈니스 로직을 건드리지 않으면서", 앞단에 권한 체크 로직을 추가하는 것**

이를 위해 최종적으로 **AOP와 전략 패턴**을 활용하여 문제를 해결하였습니다.

### 전체 흐름

```
Request 
  ↓
Aspect (ID 추출 → DB 조회 → 검증)
  ↓
Orchestrator (순수 비즈니스 로직)
  ↓
Response
```

**핵심 아이디어:**
- 메서드 실행 **전**에 Aspect가 가로채서 권한 체크 수행
- 비즈니스 로직은 그대로 유지
- Annotation만 붙이면 자동으로 검증 수행

---

## 4.2 AOP란?

### AOP (Aspect-Oriented Programming)

**정의:**
- 횡단 관심사(Cross-cutting Concerns)를 분리하여 모듈화하는 프로그래밍 패러다임
- 로깅, 보안, 트랜잭션 등 여러 곳에서 반복되는 코드를 한 곳에서 관리

**예시:**
```java
// 비즈니스 로직
public LectureResponse updateLecture(Long lectureId) {
    lecture.update(...);  // 핵심 로직만
    return lectureResponse;
}

// AOP가 자동으로 추가하는 로직
@Around("@annotation(CheckLecturePermission)")
public Object checkPermission(ProceedingJoinPoint joinPoint) {
    // 권한 체크 로직
    if (!hasPermission()) throw new AccessDeniedException();
    // 원래 메서드 실행
    return proceed();
}
```

**장점:**
- ✅ 비즈니스 로직과 보안 로직 분리
- ✅ 코드 중복 제거
- ✅ 유지보수 용이

---

## 4.3 전략 패턴이란?

### 전략 패턴 (Strategy Pattern)

**정의:**
- 알고리즘을 정의하고 각각을 캡슐화하여 상호 교환 가능하게 만드는 패턴
- 클라이언트는 전략을 선택하여 사용

**우리 상황에 적용:**
- 검증 기준이 "사용자 본인"이냐 "소속 그룹"이냐에 따라 다름
- 각 검증 전략을 Enum으로 정의하여 선택적으로 사용

**예시:**
```java
public enum DataPermissionCheckType {
    USER {
        @Override
        public void validate(ResourceOwnership resource, UserInfo user) {
            // 사용자 ID로 검증
            if (!resource.getOwnershipId().equals(user.getUserId())) {
                throw new AccessDeniedException();
            }
        }
    },
    GROUP {
        @Override
        public void validate(ResourceOwnership resource, UserInfo user) {
            // 그룹 ID로 검증
            if (!resource.getOwnershipId().equals(user.getGroupId())) {
                throw new AccessDeniedException();
            }
        }
    };
    
    public abstract void validate(ResourceOwnership resource, UserInfo user);
}
```

**장점:**
- ✅ 도메인별 다른 검증 로직을 유연하게 처리
- ✅ 새로운 검증 타입 추가가 쉬움

---

## 4.4 해결 방법 요약

### 핵심 구성 요소

1. **Annotation**: 검증이 필요한 메서드에 표시
   ```java
   @CheckLecturePermission
   public LectureResponse updateLecture(@PermissionId Long lectureId) { ... }
   ```

2. **Aspect**: Annotation이 붙은 메서드를 가로채서 검증 수행
   ```java
   @Around("@annotation(CheckDataPermission)")
   public Object checkPermission(ProceedingJoinPoint joinPoint) {
       // 1. 사용자 정보 획득
       // 2. ID 파라미터 추출
       // 3. 도메인 조회
       // 4. 검증 수행
   }
   ```

3. **전략 패턴**: 도메인별 다른 검증 로직 처리
   ```java
   checkDataPermission.type().validate(resource, user);
   ```

### 최종 코드 예시

**Before (시도 1, 2):**
```java
public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
    Lecture lecture = lectureService.findById(lectureId);
    if (!lecture.getCreatedBy().equals(userInfo.getUserId())) {
        throw new AccessDeniedException(); // 보안 로직
    }
    lecture.update(...);  // 비즈니스 로직
    return lectureResponse;
}
```

**After (AOP + 전략 패턴):**
```java
@CheckLecturePermission
public LectureResponse updateLecture(@PermissionId Long lectureId) {
    // 순수 비즈니스 로직만
    Lecture lecture = lectureService.findById(lectureId);
    lecture.update(...);
    return lectureResponse;
}
```

**차이점:**
- ✅ 보안 로직이 비즈니스 로직에서 완전히 분리됨
- ✅ Annotation만 붙이면 자동으로 검증 수행
- ✅ 코드 가독성 향상

---

## 4.5 흐름도

### 상세 흐름

```
1. Controller에서 메서드 호출
   ↓
2. Aspect가 @CheckDataPermission Annotation 감지
   ↓
3. Aspect에서 처리:
   - Request에서 사용자 정보 획득 (UserInfoInterceptor에서 설정)
   - @PermissionId가 붙은 파라미터에서 ID 추출
   - Annotation의 finder로 도메인 조회
   - Annotation의 type으로 검증 수행
   ↓
4. 검증 통과 시 원래 메서드 실행 (Orchestrator)
   ↓
5. 검증 실패 시 AccessDeniedException 발생
```

### 코드 흐름

```java
// 1. Controller
@CheckLecturePermission
public LectureResponse updateLecture(@PermissionId Long lectureId) {
    return lectureOrchestrator.updateLecture(lectureId);
}

// 2. Aspect (자동 실행)
@Around("@annotation(CheckDataPermission)")
public Object checkPermission(ProceedingJoinPoint joinPoint) {
    UserInfo user = extractUserInfo();  // Request에서 가져오기
    Long id = extractPermissionId(...);
    ResourceOwnership resource = finder.searchById(id);
    type.validate(resource, user);  // 검증
    return joinPoint.proceed();  // 원래 메서드 실행
}

// 3. Orchestrator (순수 비즈니스 로직)
public LectureResponse updateLecture(Long lectureId) {
    Lecture lecture = lectureService.findById(lectureId);
    lecture.update(...);
    return lectureResponse;
}
```

---

## 정리

✅ **AOP를 활용하면 비즈니스 로직을 건드리지 않고 보안 로직을 추가할 수 있다**

✅ **전략 패턴으로 도메인별 다른 검증 로직을 유연하게 처리할 수 있다**

✅ **Annotation만 붙이면 자동으로 검증이 수행되어 코드가 깔끔해진다**

다음 챕터에서 구체적인 구현 방법을 단계별로 알아보겠습니다.
