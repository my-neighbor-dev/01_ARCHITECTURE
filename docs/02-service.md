# 2. 문제 상황 파악

## 질문

> **"Service가 뚱뚱해지는 이유는 무엇일까?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- 하나의 Service에 모든 책임이 모이는 이유는?
- 유저 조회, 토큰 생성, 쿠키 생성, DTO 변환... 모두 한 곳에 있어야 할까?

---

## 2.1 아무 생각없이 만드는 코드 (Before)

### 문제 상황 1: 구체 클래스 참조

```java
// user-service class
@Service
public class UserServiceImpl {
    public User getUserById(Long id) { ... }
}

// auth-service에서 UserService 사용하려면?
@Service
public class AuthServiceImpl {
    private final UserServiceImpl userService;  // ❌ 구체 클래스 참조
    // → auth-service 모듈이 user-service 모듈에 의존해야 함
    // → 순환 의존성 발생 가능
    // → 모듈 분리 불가능
}
```

### 현직자의 시선

- "auth 모듈이 user-service-impl 모듈을 참조해야 한다고?"
- "이렇게 하면 모듈을 분리할 수 없는데..."

### 문제점

1. **다른 모듈에서 구체 클래스를 참조해야 함**
2. **모듈 간 강한 결합**
3. **모듈 분리 시 사용 불가능**

---

## 2.2 Service가 뚱뚱해지는 이유

### 문제 상황 2: 뚱뚱한 Service

```java
// Before: 뚱뚱한 Service
@Service
public class AuthService {
    // 유저 정보 조회
    public User getUserById(Long id) { ... }
    
    // 로그인 로직
    public LoginResponse login(LoginRequest request) {
        User user = getUserById(...);  // 유저 조회
        String token = generateToken(user);  // 토큰 생성
        Cookie cookie = createCookie(token);  // 쿠키 생성
        return new LoginResponse(...);  // DTO 변환
    }
    
    // 모든 책임이 한 곳에!
}
```

### 현직자의 시선

- "이 Service는 뭘 하는 거지?"
- "유저 조회, 토큰 생성, 쿠키 생성... 다 여기 있네?"

### 문제점

1. **단일 책임 원칙 위반**
   - 유저 조회, 토큰 생성, 쿠키 생성, DTO 변환... 모든 책임이 한 곳에

2. **재사용 불가능**
   - 토큰 생성 로직만 필요해도 전체 Service를 주입받아야 함 -> 재사용이 어려움

3. **유지보수 어려움**
   - 로그인 로직을 수정하려면 이 Service만 수정해야 함
   - 다른 기능에 영향이 갈 수 있음

---

## 2.3 핵심 문제 정리

### 문제 1: 모듈 간 의존성

```
auth service → user-service (구체 클래스 참조)
```

**결과**: 모듈 분리 불가능, 순환 의존성 발생 가능

### 문제 2: Service 책임 과다

```
AuthService {
    - 유저 조회
    - 토큰 생성
    - 쿠키 생성
    - DTO 변환
    - 비즈니스 로직
}
```

**결과**: 재사용 불가능, 유지보수 어려움

---

## 다음 단계

이제 이 문제들을 어떻게 해결할 수 있는지 알아봅시다!

1. **Repository 인터페이스화**: Service가 기술에 의존하지 않도록, 기술 스택 유연하게 교체
2. **Orchestrator 패턴**: Service 책임 분리
3. **외부 모듈 호출**: Feign 패턴으로 기술 의존성 분리
