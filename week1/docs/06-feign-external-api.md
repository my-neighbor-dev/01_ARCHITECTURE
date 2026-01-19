# 6. 외부 API 호출: Feign 패턴

## 질문

> **"다른 모듈의 API를 호출할 때 Service가 Feign Client를 직접 사용해도 될까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- Service에서 Feign Client를 직접 사용하면 어떤 문제가 생길까?
- Service가 기술에 의존하지 않으려면 어떻게 해야 할까?
- 모듈 간 결합을 어떻게 약하게 만들 수 있을까?

---

## 6.1 Feign Client란?

**Feign Client**는 Spring Cloud에서 제공하는 HTTP 클라이언트 라이브러리입니다.

### Feign Client의 특징

- **선언적 HTTP 클라이언트**: 인터페이스만 정의하면 자동으로 HTTP 요청을 생성
- **서비스 간 통신**: 마이크로서비스나 모듈 간 API 호출에 사용
- **자동 매핑**: 메서드 시그니처를 HTTP 요청으로 자동 변환

### Feign Client 사용 예시

```java
@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserApiFeignClient {
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}

// 사용
@Autowired
private UserApiFeignClient userApiFeignClient;

public void someMethod() {
    UserResponse user = userApiFeignClient.getUserById(1L);
    // 자동으로 http://localhost:8080/api/users/1 호출
}
```

**핵심**: Feign Client는 **기술적인 구현 세부사항**입니다. Service가 이를 직접 의존하면 기술에 종속됩니다.

---

## 6.2 문제 상황

Auth 모듈에서 User 모듈의 API를 호출해야 하는 경우:

### Before: Service가 Feign Client를 직접 의존

```java
// auth-service 모듈
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // ❌ Feign Client 구현체를 직접 의존
    private final UserApiFeignClient userApiFeignClient;
    
    public LoginResult login(LoginRequest request) {
        // 1. 유저 조회 (Feign Client 직접 사용)
        AuthUserResponse authUserResponse = userApiFeignClient.getUserByEmail(request.getEmail());
        
        // 2. DTO를 Domain 객체로 변환
        AuthUser authUser = toAuthUser(authUserResponse);
        
        // 3. 비밀번호 검증
        if (!authUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // 4. 토큰 생성
        AuthToken authToken = generateToken(authUser);
        
        return new LoginResult(authUser, authToken);
    }
    
    private AuthUser toAuthUser(AuthUserResponse response) {
        return new AuthUser(
            response.getId(),
            response.getEmail(),
            response.getPassword()
        );
    }
}

// Feign Client 정의
@FeignClient(name = "lecture-service", url = "${lecture.service.url:http://localhost:8080}")
public interface UserApiFeignClient {
    @GetMapping("/api/users/by-email/{email}")
    AuthUserResponse getUserByEmail(@PathVariable("email") String email);
}
```

**문제점**:
- ❌ **Service가 기술(Feign Client)에 의존**: `UserApiFeignClient`는 Feign이라는 기술 구현체
- ❌ **기술 스택 교체 어려움**: Feign을 다른 HTTP 클라이언트로 바꾸려면 Service 코드를 수정해야 함
- ❌ **비즈니스 로직과 기술 세부사항 혼재**: DTO 변환 로직도 Service에 포함됨
- ❌ **테스트 어려움**: Feign Client를 Mock하기 어려움

**핵심 문제**: 
> **Service는 비즈니스 로직만 담당해야 하는데, Feign Client라는 기술 구현체에 의존하고 있음**

---

## 6.3 해결: 외부 API 인터페이스 + Feign

### 구조

```
Auth 모듈:
- AuthOrchestrator → AuthService → AuthUserApi (인터페이스)
                                          ↓
                              AuthUserApiFeign (Feign 구현체)
                                          ↓
                              UserApiFeignClient (Feign Client)
                                          ↓
                              UserApi (실제 API 호출)
```

### 1단계: 외부 API 인터페이스 생성

```java
// auth-external-api 모듈
public interface AuthUserApi {
    AuthUser getUserByEmail(String email);
}
```

**장점**:
- Auth 모듈에 필요한 인터페이스만 정의
- User 모듈의 내부 구현과 분리
- `AuthUser` 도메인만 참조 (user-domain 의존성 제거)

### 2단계: Feign 구현체 생성

```java
// auth-external-api-using-feign 모듈
@Component
@RequiredArgsConstructor
public class AuthUserApiFeign implements AuthUserApi {
    
    private final UserApiFeignClient userApiFeignClient;
    
    @Override
    public AuthUser getUserByEmail(String email) {
        // 1. Feign Client를 통해 UserApi 호출 (DTO 반환)
        AuthUserResponse authUserResponse = userApiFeignClient.getUserByEmail(email);
        
        // 2. DTO를 Domain 객체로 변환
        return toAuthUser(authUserResponse);
    }
    
    private AuthUser toAuthUser(AuthUserResponse authUserResponse) {
        return new AuthUser(
            authUserResponse.getId(),
            authUserResponse.getEmail(),
            authUserResponse.getPassword()
        );
    }
    
    // Feign Client 인터페이스
    @FeignClient(name = "lecture-service", url = "${lecture.service.url:http://localhost:8080}")
    public interface UserApiFeignClient {
        @GetMapping("/api/users/by-email/{email}")
        AuthUserResponse getUserByEmail(@PathVariable("email") String email);
    }
}
```

**주의사항**:
- Feign Client 인터페이스는 `@RequestMapping`을 사용할 수 없음
- `@GetMapping`에 전체 경로를 명시해야 함
- `@PathVariable`에 value를 명시해야 함

### 3단계: Service에서 인터페이스 사용

```java
// auth-service 모듈
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // ✅ 인터페이스만 참조 - Feign 구현체를 직접 참조하지 않음
    private final AuthUserApi authUserApi;  
    
    public LoginResult login(LoginRequest request) {
        // 1. 유저 조회 (인터페이스를 통해 호출)
        //    실제 구현체(AuthUserApiFeign)는 Spring이 주입
        //    Service는 Feign Client를 몰라도 됨!
        AuthUser authUser = authUserApi.getUserByEmail(request.getEmail());
        
        if (authUser == null) {
            throw new RuntimeException("User not found");
        }
        
        // 2. 비밀번호 검증
        if (!authUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // 3. 토큰 생성
        AuthToken authToken = generateToken(authUser);
        
        return new LoginResult(authUser, authToken);
    }
    
    private AuthToken generateToken(AuthUser authUser) {
        String token = UUID.randomUUID().toString();
        Long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        return new AuthToken(token, authUser.getId(), expiresAt);
    }
}
```

**핵심**: 
- ✅ **Service는 인터페이스만 참조**: `AuthUserApi` 인터페이스만 의존
- ✅ **Feign 구현체는 별도 모듈에 위치**: `AuthUserApiFeign`은 `auth-external-api-using-feign` 모듈에 위치
- ✅ **Service는 기술(Feign Client)에 의존하지 않음**: Feign을 다른 기술로 바꿔도 Service 코드는 변경 불필요
- ✅ **비즈니스 로직에만 집중**: 유저 조회, 비밀번호 검증, 토큰 생성만 담당

---


## 6.4 실무 적용 예시

### 예시: 주문 서비스에서 결제 서비스 호출

```java
// order-external-api 모듈
public interface PaymentApi {
    PaymentResult processPayment(PaymentRequest request);
}

// order-external-api-using-feign 모듈
@Component
@RequiredArgsConstructor
public class PaymentApiFeign implements PaymentApi {
    private final PaymentApiFeignClient paymentApiFeignClient;
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        PaymentResponse response = paymentApiFeignClient.processPayment(request);
        return toPaymentResult(response);
    }
    
    @FeignClient(name = "payment-service", url = "${payment.service.url}")
    public interface PaymentApiFeignClient {
        @PostMapping("/api/payments/process")
        PaymentResponse processPayment(@RequestBody PaymentRequest request);
    }
}
```

---
## 실습

`AuthUserApi` 인터페이스 확인

`AuthUserApiFeign` 구현체 확인

---

## 정리

✅ **Service는 기술(Feign Client)에 의존하지 않아야 한다**

✅ **외부 모듈 호출은 인터페이스 + Feign 패턴 사용**

✅ **Service는 인터페이스만 참조하고, Feign 구현체는 별도 모듈에 위치**
