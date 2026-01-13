# 5. API 인터페이스 계층

## 질문

> **"Swagger 설정 때문에 Controller가 복잡하지 않나요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- API 스펙을 코드로 어떻게 명확하게 정의할 수 있을까?
- 다른 서비스에서 API를 호출할 때 어떻게 간단하게 API 스펙만 알 수 있을까?

---

## 5.1 문제 상황: Swagger 설정이 Controller에 섞임

### Before: Swagger 설정이 Controller에 섞인 복잡한 구조

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserOrchestrator userOrchestrator;
    
    @Operation(
        summary = "유저 정보 조회",
        description = "유저 ID를 통해 유저 정보를 조회합니다.",
        tags = {"User"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "유저 정보 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class),
                examples = @ExampleObject(
                    name = "성공 예시",
                    value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"홍길동\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "유저를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getUserById(
        @Parameter(
            description = "유저 ID",
            required = true,
            example = "1",
            schema = @Schema(type = "integer", format = "int64")
        )
        @PathVariable Long id,
        @Parameter(hidden = true)
        HttpServletRequest request
    ) {
        // 로깅
        log.info("유저 조회 요청: userId={}, ip={}", id, request.getRemoteAddr());
        
        // 권한 처리 (이미 @PreAuthorize로 처리되지만 예시)
        // ...
        
        // 비즈니스 로직 호출
        UserResponse response = userOrchestrator.getUserById(id);
        
        // 로깅
        log.info("유저 조회 완료: userId={}", id);
        
        return ResponseEntity.ok(response);
    }
}
```

**문제점**:
- ❌ **Controller가 복잡해짐**: Swagger 어노테이션(`@Operation`, `@ApiResponses`, `@Parameter` 등)이 Controller에 섞여있어 코드가 복잡해짐
- ❌ **Controller의 본질적 역할이 흐려짐**: Controller는 **로깅, 권한처리** 같은 역할만 수행해야 하는데, Swagger 문서화 관련 코드가 섞여있음
- ❌ **관심사 분리 실패**: API 스펙 정의(문서화)와 HTTP 요청/응답 처리(로깅, 권한)가 한 곳에 섞임

**Controller의 본질적 역할**:
- ✅ **로깅**: 요청/응답 로깅
- ✅ **권한 처리**: 인증/인가 처리
- ✅ **HTTP 요청/응답 처리**: 요청 파라미터 파싱, 응답 생성

**Swagger 설정은 Controller의 본질적 역할이 아님!**

---

## 5.2 API 인터페이스란?

**API 인터페이스를 만드는 이유:**

1. **API 스펙을 코드로 명확하게 정의**
   - Swagger 어노테이션을 API 인터페이스에만 집중
   - Controller는 로깅, 권한처리 같은 본질적 역할만 수행

2. **다른 서비스에서 API 인터페이스만 공유**
   - 구현체(Controller)는 공유하지 않아도 됨
   - API 인터페이스만 의존하면 API 스펙을 알 수 있음

3. **타입 안정성**
   - API가 변경되면 컴파일 에러로 바로 알 수 있음
   - 문서와 코드의 불일치 방지

---

## 5.3 API 인터페이스 구조

### API 인터페이스 구조

```
user-api/
├── UserApi.java        ← API 인터페이스 (Swagger 어노테이션 포함)
├── UserResponse.java   ← DTO (일반 조회용)
└── AuthUserResponse.java  ← DTO (인증용, password 포함)
```

**장점:**
- API 스펙이 코드에 명확하게 정의됨
- Swagger 문서 자동 생성
- 다른 서비스에서 인터페이스만 보면 API 스펙을 알 수 있음

---

## 5.4 API 인터페이스 구현

### UserApi 인터페이스

```java
@Tag(name = "User", description = "유저 관련 API")
@RequestMapping("/api/users")
public interface UserApi {
    
    @Operation(
        summary = "유저 정보 조회",
        description = "유저 ID를 통해 유저 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "유저 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "유저를 찾을 수 없음"
        )
    })
    @GetMapping("/{id}")
    UserResponse getUserById(
        @Parameter(description = "유저 ID", required = true, example = "1")
        @PathVariable Long id
    );
}
```

### UserController 구현

```java
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {
    
    private final UserOrchestrator userOrchestrator;
    
    @Override
    public UserResponse getUserById(Long id) {
        // ✅ Controller의 본질적 역할: 로깅
        log.info("유저 조회 요청: userId={}", id);
        
        // ✅ Controller의 본질적 역할: 비즈니스 로직 호출
        UserResponse response = userOrchestrator.getUserById(id);
        
        log.info("유저 조회 완료: userId={}", id);
        return response;
    }
}
```

**핵심**: 
- ✅ **Controller는 깔끔함**: Swagger 설정은 API 인터페이스에만 있고, Controller는 로깅과 비즈니스 로직 호출만 담당
- ✅ **Controller의 본질적 역할**: 로깅, 권한처리(필요시), HTTP 요청/응답 처리
- ✅ **API 인터페이스가 Swagger 설정 담당**: API 스펙 정의는 인터페이스에서만 처리
- ✅ **관심사 분리**: 문서화(Swagger)와 HTTP 처리(Controller)가 분리됨

---

## 5.5 다른 서비스에서 사용하기

### 시나리오: lecture-client 서비스

**lecture-client 서비스**가 **lecture-service**의 API를 호출하려면?

### 방법 1: API 인터페이스만 의존

```kotlin
// lecture-client/build.gradle.kts
dependencies {
    // API 인터페이스만 의존! (구현체는 의존하지 않음)
    implementation(project(":user:user-api"))
    implementation(project(":auth:auth-api"))
    
    // user-controller, auth-controller는 의존하지 않음!
}
```

### 방법 2: Feign Client로 API 호출

```java
@FeignClient(name = "lecture-service", url = "http://localhost:8080")
public interface UserApiClient extends UserApi {
    // UserApi 인터페이스를 그대로 상속받아 사용
    // → API 스펙이 자동으로 적용됨
}
```

### 사용 예시

```java
@RestController
public class ClientController {
    
    @Autowired
    private UserApiClient userApiClient;  // UserApi 인터페이스만 의존
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        // API 인터페이스를 통해 호출
        // → API 스펙이 코드에 명확하게 정의되어 있음
        return userApiClient.getUserById(id);
    }
}
```

---

## 5.6 장점 정리

### 1. API 스펙 명확성

- API 계층 코드만 봐도 API 스펙을 알 수 있음

### 2. 모듈 독립성

- 다른 서비스에서 API 인터페이스만 의존
- 구현체(Controller)는 공유하지 않아도 됨

### 3. 타입 안정성

- API가 변경되면 컴파일 에러로 바로 알 수 있음
- 문서와 코드의 불일치 방지

### 4. 재사용성

- 여러 서비스에서 같은 API 인터페이스 사용 가능

---

## 5.7 Swagger UI 확인

애플리케이션 실행 후:

```
http://localhost:8080/swagger-ui.html
```

에서 API 문서를 확인할 수 있습니다.

---

## 실습
`UserApi` 인터페이스 확인

Swagger UI 확인 (선택사항)

---

## 정리

✅ **API 인터페이스를 만들면 API 스펙이 코드로 명확하게 정의된다**

✅ **다른 서비스에서 API 인터페이스만 공유하면 API 스펙을 알 수 있다**

✅ **타입 안정성을 통해 API 변경 시 컴파일 에러로 바로 알 수 있다**
