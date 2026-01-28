# 3. 시도 1, 2: 단순한 접근의 한계

## 질문

> **"모든 메서드에 같은 검증 로직을 넣으면 어떤 문제가 생길까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- 수십 개의 메서드마다 똑같은 `if (resource.createdBy != user.id) throw AccessDeniedException()` 코드를 넣어야 한다면?
- 비즈니스 로직과 보안 로직이 섞이면 코드 가독성이 어떻게 될까?
- 검증 로직을 수정해야 할 때 몇 군데를 수정해야 할까?

---

## 3.1 시도 1: 모든 서비스 메서드에 check() 로직 넣기

### 가장 직관적인 방법

데이터 소유권 검증이 필요한 모든 메서드 상단에 검증 로직을 넣는 것입니다.

**코드 예시:**

```java
public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
    Lecture lecture = lectureService.findById(lectureId);

    // 검증 로직 직접 주입
    if (!lecture.getCreatedBy().equals(userInfo.getUserId())) {
        throw new AccessDeniedException("Access Denied");
    }

    // 비즈니스 로직
    lecture.update(...);
    lectureRepository.save(lecture);
    return lectureResponse;
}
```

### 문제점

하지만 이 방식은 치명적인 단점이 있었습니다.

#### 1. 중복 코드

**문제:**
- 수십 개의 서비스 메서드마다 똑같은 `if` 문을 복사-붙여넣기 해야 합니다.

**예시:**
```java
public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
    Lecture lecture = lectureService.findById(lectureId);
    if (!lecture.getCreatedBy().equals(userInfo.getUserId())) {
        throw new AccessDeniedException(); // 중복
    }
    // ...
}

public void deleteLecture(Long lectureId, UserInfo userInfo) {
    Lecture lecture = lectureService.findById(lectureId);
    if (!lecture.getCreatedBy().equals(userInfo.getUserId())) {
        throw new AccessDeniedException(); // 중복
    }
    // ...
}
```

**현직자의 시선:**
- "이런 코드가 50개 메서드에 있다면? 검증 로직을 수정하려면 50군데를 수정해야 해"

#### 2. 가독성 저하

**문제:**
- 핵심 비즈니스 로직과 인가 로직이 섞여 코드가 지저분해집니다.

**예시:**
```java
public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
    // 인가 로직 (보안)
    Lecture lecture = lectureService.findById(lectureId);
    if (!lecture.getCreatedBy().equals(userInfo.getUserId())) {
        throw new AccessDeniedException();
    }
    
    // 비즈니스 로직 (핵심)
    lecture.update(...);
    lectureRepository.save(lecture);
    
    // 인가 로직과 비즈니스 로직이 섞여 있음
    return lectureResponse;
}
```

**현직자의 시선:**
- "이 메서드가 뭘 하는지 한눈에 파악하기 어렵네. 보안 로직 때문에 비즈니스 로직이 가려져"

---

## 3.2 시도 2: 공통 검증 로직 사용하기

### 중복 코드 해결 시도

중복 코드가 발생하는 문제를 해결하기 위해서 도메인 객체를 조회한 이후에 enum 값을 통해 검증 타입을 명시하여 검증 타입에 맞는 공통의 검증 로직을 사용하고자 하였습니다.

**코드 예시:**

```java
// 공통 검증 로직
public class AuthenticateValidator {
    public void validateByUser(Long resourceCreatedBy, Long userId) {
        if (!resourceCreatedBy.equals(userId)) {
            throw new AccessDeniedException("Access Denied");
        }
    }
    
    public void validateByGroup(Long resourceGroupId, Long userGroupId) {
        if (!resourceGroupId.equals(userGroupId)) {
            throw new AccessDeniedException("Access Denied");
        }
    }
}

// 사용
public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
    Lecture lecture = lectureService.findById(lectureId);

    // 공통으로 사용하는 검증 로직 호출
    authenticateValidator.validateByUser(lecture.getCreatedBy(), userInfo.getUserId());

    // 비즈니스 로직
    lecture.update(...);
    lectureRepository.save(lecture);
    return lectureResponse;
}
```

### 개선된 점

✅ **중복 코드 문제 해결**
- 검증 로직이 한 곳에 모여 있어 수정이 쉬움

### 여전한 문제점

하지만 이 방식으로 중복 코드 문제는 어느 정도 해결되었지만, **비즈니스 로직과 보안 로직이 섞여서 Orchestrator 계층이 복잡해지는 문제**가 여전히 존재했습니다.

**문제:**
- Orchestrator 메서드마다 검증 로직을 호출해야 함
- 비즈니스 로직과 보안 로직이 여전히 섞여 있음

**예시:**
```java
public LectureResponse updateLecture(Long lectureId, UserInfo userInfo) {
    // 보안 로직
    Lecture lecture = lectureService.findById(lectureId);
    authenticateValidator.validateByUser(lecture.getCreatedBy(), userInfo.getUserId());
    
    // 비즈니스 로직
    lecture.update(...);
    lectureRepository.save(lecture);
    return lectureResponse;
}

public LectureResponse deleteLecture(Long lectureId, UserInfo userInfo) {
    // 보안 로직
    Lecture lecture = lectureService.findById(lectureId);
    authenticateValidator.validateByUser(lecture.getCreatedBy(), userInfo.getUserId());
    
    // 비즈니스 로직
    lecture.delete();
    lectureRepository.save(lecture);
    return lectureResponse;
}
```

**현직자의 시선:**
- "중복 코드는 줄었지만, 여전히 모든 메서드에 검증 로직을 넣어야 하네"
- "비즈니스 로직만 보고 싶은데 보안 로직 때문에 방해받아"

---

## 3.3 이상적인 해결책

### 목표

**"기존 비즈니스 로직을 건드리지 않으면서", 앞단에 권한 체크 로직을 추가하는 것**

**이상적인 코드:**
```java
// Annotation만 붙이면 자동으로 검증 수행
@CheckLecturePermission
public LectureResponse updateLecture(@PermissionId Long lectureId) {
    // 순수 비즈니스 로직만
    Lecture lecture = lectureService.findById(lectureId);
    lecture.update(...);
    lectureRepository.save(lecture);
    return lectureResponse;
}
```

**장점:**
- ✅ 비즈니스 로직과 보안 로직 완전 분리
- ✅ 코드 중복 제거
- ✅ 가독성 향상
- ✅ 유지보수 용이

---

## 정리

### 시도 1의 문제점
- ❌ 중복 코드: 수십 개의 메서드마다 똑같은 검증 로직
- ❌ 가독성 저하: 비즈니스 로직과 보안 로직 혼재

### 시도 2의 문제점
- ✅ 중복 코드 해결
- ❌ 비즈니스 로직과 보안 로직 여전히 혼재

### 이상적인 해결책
- ✅ 비즈니스 로직을 건드리지 않고 보안 로직 추가
- ✅ Annotation 기반으로 자동 검증

다음 챕터에서 AOP와 전략 패턴을 활용하여 이 문제를 어떻게 해결했는지 알아보겠습니다.
