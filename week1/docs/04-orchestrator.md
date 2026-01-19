# 4. Orchestrator 패턴 도입

## 질문

> **"여러 Service를 조율하는 계층이 필요한가?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- Controller에서 여러 Service를 직접 호출하면 안 될까?
- Service를 재사용하려면 어떻게 해야할까?
- 비즈니스 로직과 DTO 조립을 어떻게 분리할까?

---

## 4.1 Service 책임 분리

### Before: 뚱뚱한 Service

```java
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

**문제**: 모든 책임이 한 곳에 모임

### After: Service 책임 분리

```java
// user-service 모듈
@Service
public class UserService {
    User getUserById(Long id);  // 단일 책임: 유저 조회만
}

// auth-service 모듈
@Service
public class AuthService {
    LoginResult login(LoginRequest request);  // 비즈니스 로직: 유저 조회, 비밀번호 검증, 토큰 생성
}

// auth-service 모듈
@Service
public class CookieService {
    Cookie createCookie(String token);  // 단일 책임: 쿠키 생성만
}
```

**해결**: 
- 각 Service는 단일 책임만 수행
- AuthService는 로그인 비즈니스 로직 처리

---

## 4.2 왜 Orchestrator가 필요한가?

### 문제 1: Controller에서 여러 Service를 직접 호출하면?

```java
@RestController
public class AuthController {
    
    private final AuthService authService;
    private final CookieService cookieService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // ❌ Controller에서 여러 Service를 직접 호출
        LoginResult loginResult = authService.login(request);
        Cookie cookie = cookieService.createCookie(loginResult.getToken());
        
        return ResponseEntity.ok(new LoginResponse(...));
    }
}
```

**문제점**:
- **트랜잭션으로 묶을 수 없음**: Controller는 여러 도메인(Service)을 합치는 구조가 되고, 보통 Service에 `@Transactional`을 붙이는데, Controller에서 여러 Service를 호출하면 각각의 트랜잭션으로 분리됨
- **한 응답을 처리하는데 트랜잭션으로 안 묶임**: 예를 들어, 주문 생성 시 재고 확인 → 주문 생성 → 결제 처리를 하나의 트랜잭션으로 묶을 수 없음

### 해결 1: Service가 Service를 참조하면?

```java
@Service
@Transactional  // ✅ 트랜잭션으로 묶을 수 있음
public class AuthService {
    
    private final UserService userService;
    private final TokenService tokenService;
    
    public LoginResponse login(LoginRequest request) {
        // 여러 Service를 하나의 트랜잭션으로 묶을 수 있음
        User user = userService.getUserByEmail(request.getEmail());
        String token = tokenService.generateToken(user);
        
        // ❌ 하지만 Response까지 만들면?
        return new LoginResponse(user.getId(), token, ...);  // Service가 특정 Response에 의존
    }
}
```

**문제점**:
- **Service가 커짐**: 비즈니스 로직뿐만 아니라 DTO 조립까지 담당하게 됨
- **재사용이 어려움**: `LoginResponse`에 특화되어 있어서, 다른 곳에서 `AuthService.login()`을 호출해도 항상 `LoginResponse`를 받게 됨
- **특정 Response에 의존**: Service가 특정 API 응답 형식에 의존하게 되어 재사용성이 떨어짐

### 해결 2: Orchestrator 패턴

```java
// Service: 비즈니스 로직만 담당 (트랜잭션 가능)
@Service
@Transactional
public class AuthService {
    
    private final AuthUserApi authUserApi;
	
    public LoginResult login(LoginRequest request) {
        // ✅ 비즈니스 로직만 처리
        User user = authUserApi.getUserByEmail(request.getEmail());
        String token = generateToken(user);
        
        return new LoginResult(user, token);  // 도메인 모델 반환
    }
}

// Orchestrator: DTO 조립만 담당
@Component
public class AuthOrchestrator {
    
    private final AuthService authService;
    private final CookieService cookieService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. 비즈니스 로직 처리 (트랜잭션 내에서)
        LoginResult loginResult = authService.login(request);
        
        // 2. 쿠키 생성
        Cookie cookie = cookieService.createCookie(loginResult.getToken());
        
        // 3. DTO 조립
        return new LoginResponse(
            loginResult.getUser().getId(),
            loginResult.getToken(),
            cookie
        );
    }
}
```

**장점**:
- ✅ **Service는 트랜잭션으로 묶을 수 있음**: Service가 Service를 참조하므로 하나의 트랜잭션으로 처리 가능
- ✅ **Service는 비즈니스 로직만 담당**: Response에 의존하지 않아 재사용 가능
- ✅ **Orchestrator는 DTO 조립만 담당**: 특정 API 응답 형식에 맞춰 조립

---

## 4.3 Orchestrator로 조율

### Orchestrator란?

**여러 Service를 조율하여 비즈니스 유스케이스를 완성하는 계층**

### AuthOrchestrator 구현

```java
// auth-orchestrator 모듈
@Component
@RequiredArgsConstructor
public class AuthOrchestrator {
    
    private final AuthService authService;  // Service 구현체
    private final CookieService cookieService;  // Service 구현체
    
    /**
     * 로그인 유스케이스: 여러 Service를 조율하여 DTO 조립
     */
    public LoginResponse login(LoginRequest request) {
        // 1. AuthService를 통해 로그인 처리 (비즈니스 로직: 유저 조회, 비밀번호 검증, 토큰 생성)
        LoginResult loginResult = authService.login(request);
        
        // 2. 쿠키 생성
        Cookie cookie = cookieService.createCookie(loginResult.getAuthToken().getToken());
        
        // 3. DTO 조립
        return new LoginResponse(
            loginResult.getAuthUser().getId(),
            loginResult.getAuthToken().getToken(),
            cookie
        );
    }
}
```

**구조**:
- **AuthService**: 비즈니스 로직 처리 (유저 조회, 비밀번호 검증, 토큰 생성)
- **AuthOrchestrator**: DTO 조립만 담당

---

## 4.4 각 Service 구현

### UserService 구현

```java
// user-service 모듈 (구현체만, 인터페이스 없음)
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;  // ✅ Repository는 인터페이스
    
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
```

### AuthService 구현

```java
// auth-service 모듈
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // ✅ 외부 API 인터페이스 사용 - Service가 기술(Feign)에 의존하지 않음
    private final AuthUserApi authUserApi;
    
    public LoginResult login(LoginRequest request) {
        // 1. 유저 조회 (인터페이스를 통해 호출)
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

**핵심 원칙**:
- **Service는 비즈니스 로직만 담당**
- **Service는 기술에 의존하지 않음** (인터페이스만 참조)
- **Repository**: 항상 인터페이스 사용 (기술 스택 교체 가능)

### CookieService 구현

```java
// auth-service 모듈
@Service
public class CookieService {
    
    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24시간
        return cookie;
    }
}
```

---

## 4.5 Controller에서 사용

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthOrchestrator authOrchestrator;  // Orchestrator 사용
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        LoginResponse loginResponse = authOrchestrator.login(request);
        
        // 쿠키 설정
        response.addCookie(loginResponse.getCookie());
        
        return ResponseEntity.ok(loginResponse);
    }
}
```

---

## 4.6 장점

### 1. 각 Service는 단일 책임만 수행

- **UserService**: 유저 조회만
- **AuthService**: 토큰 생성만
- **CookieService**: 쿠키 생성만

### 2. Orchestrator가 여러 Service를 조율

- 로그인 유스케이스를 완성
- 각 Service를 순서대로 호출
- DTO 조립

### 3. 재사용 가능

- **UserService**: 다른 곳에서도 유저 조회 가능
- **AuthService**: 다른 곳에서도 토큰 생성 가능
- **CookieService**: 다른 곳에서도 쿠키 생성 가능

---

## 4.7 계층 구조

```
Controller
    ↓
Orchestrator  ← 여러 Service 조율
    ↓
Service (인터페이스)  ← 단일 책임
    ↓
Repository (인터페이스)  ← 데이터 접근
```

### 각 계층의 역할

- **Controller**: HTTP 요청/응답 처리
- **Orchestrator**: 여러 Service 조율하여 유스케이스 완성
- **Service**: 단일 책임의 비즈니스 로직
- **Repository**: 데이터 접근

---

## 4.8 실무 적용 예시

### 예시 1: 회원가입

```java
// user-orchestrator 모듈
@Component
@RequiredArgsConstructor
public class UserOrchestrator {
    
    private final UserService userService;
    private final EmailService emailService;
    
    public UserResponse signUp(SignUpRequest request) {
        // 1. 유저 생성 (Service에서 비즈니스 로직 처리)
        User user = userService.create(request);
        
        // 2. 이메일 발송
        emailService.sendWelcomeEmail(user);
        
        // 3. DTO 조립
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName()
        );
    }
}
```

### 예시 2: 주문 생성

```java
// order-service 모듈
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    
    public OrderResult createOrder(OrderRequest request) {
        // 비즈니스 로직 처리
        // 1. 재고 확인
        inventoryService.checkStock(request.getItems());
        
        // 2. 주문 생성
        Order order = orderService.create(request);
        
        // 3. 결제 처리
        paymentService.processPayment(order);
        
        // 4. 재고 차감
        inventoryService.decreaseStock(request.getItems());
        
        return new OrderResult(order);
    }
}

// order-orchestrator 모듈
@Component
@RequiredArgsConstructor
public class OrderOrchestrator {
    
    private final OrderService orderService;
    
    public OrderResponse createOrder(OrderRequest request) {
        // 1. 비즈니스 로직 처리
        OrderResult orderResult = orderService.createOrder(request);
        
        // 2. DTO 조립
        return new OrderResponse(
            orderResult.getOrder().getId(),
            orderResult.getOrder().getTotalAmount(),
            // ...
        );
    }
}
```

---

## 실습
`AuthOrchestrator` 코드 확인

`AuthService`, `CookieService` 코드 확인

Service가 비즈니스 로직만 담당하는 것 확인

---

## 정리

✅ **Orchestrator 패턴으로 Service를 얇게 유지하고 책임을 분리한다**

✅ **Service는 비즈니스 로직만 담당하고, Orchestrator는 DTO 조립만 담당한다**

✅ **각 Service는 단일 책임만 수행하고, Orchestrator가 여러 Service를 조율한다**

✅ **재사용 가능한 구조가 된다**

