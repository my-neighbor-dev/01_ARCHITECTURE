# 7. 전체 구현

## 질문

> **"이 모든 것을 어떻게 통합할까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- Device ID 추출, Rate Limiting 체크, 로그인 처리, 쿠키 생성의 순서는?
- 각 컴포넌트를 어떻게 연결할까?
- Redis와 로컬 캐시를 어떻게 교체할 수 있을까?

---

## 7.1 전체 흐름

### 요청 처리 순서

```
1. 요청 도착
   ↓
2. DeviceIdInterceptor
   - 쿠키에서 device_id 추출/생성
   - DeviceInfo 생성 (deviceId, clientIp, userAgent)
   ↓
3. AuthController
   - DeviceInfo 주입 (ArgumentResolver)
   - AuthOrchestrator 호출
   ↓
4. AuthOrchestrator
   - AuthUserApi.getUserByEmail() (유저 조회)
   - RateLimitingService.checkRateLimit() (Rate Limit 체크)
   - AuthService.login() (로그인 처리)
   - CookieService.createCookie() (쿠키 생성)
   ↓
5. Response
   - 쿠키 설정 (Access Token, Refresh Token)
   - 사용자 정보 반환
```

---

## 7.2 코드 구조

### DeviceIdInterceptor

```java
@Component
public class DeviceIdInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        // 쿠키에서 device_id 추출/생성
        String deviceId = getOrCreateDeviceId(request, response);
        
        // DeviceInfo 생성
        DeviceInfo deviceInfo = new DeviceInfo(
            deviceId,
            extractClientIp(request),
            request.getHeader("User-Agent")
        );
        
        // Request에 저장
        request.setAttribute("deviceInfo", deviceInfo);
        return true;
    }
}
```

---

### DeviceInfoArgumentResolver

```java
@Component
public class DeviceInfoArgumentResolver implements HandlerMethodArgumentResolver {
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == DeviceInfo.class;
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest, 
                                 WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return request.getAttribute("deviceInfo");
    }
}
```

---

### AuthController

```java
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    
    private final AuthOrchestrator authOrchestrator;
	
    @Override 
    public void login(LoginRequest request, DeviceInfo deviceInfo, HttpServletResponse response) {
        LoginResultWithCookies result = authOrchestrator.login(request, deviceInfo);
		
        // 쿠키를 Response에 추가
        response.addCookie(result.getAccessTokenCookie());
        response.addCookie(result.getRefreshTokenCookie());

        // Response Body는 비움 (쿠키만으로 충분)
    }
}
```

---

### AuthOrchestrator

```java
@Component
@RequiredArgsConstructor
public class AuthOrchestrator {
    
    private final RateLimitingService rateLimitingService;
    private final AuthService authService;
    private final CookieService cookieService;

    public LoginResultWithCookies login(LoginRequest request, DeviceInfo deviceInfo) {
        // 1. 유저 조회 (email로 조회)
        AuthUser authUser = authUserApi.getUserByEmail(request.getEmail());
		
        // 2. Rate Limit 체크 (유저 조회 후 phoneNumber로 체크)
        rateLimitingService.checkRateLimit(authUser.getPhoneNumber(), deviceInfo);
		
        // 3. AuthService를 통해 로그인 처리 (비즈니스 로직: 비밀번호 검증, 토큰 생성)
        LoginResult loginResult = authService.login(authUser, request.getPassword());

        // 4. 로그인 성공 시 Rate Limit 카운트 리셋 (정상 사용자이므로 제한 해제)
        rateLimitingService.resetRateLimit(authUser.getPhoneNumber(), deviceInfo);

        // 5. 쿠키 생성
        Cookie accessTokenCookie = cookieService.createAccessTokenCookie(
            loginResult.getAccessToken().getToken()
        );
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(
            loginResult.getRefreshToken().getToken()
        );

        // 6. 쿠키만 반환 (Response Body는 비움)
        return new LoginResultWithCookies(accessTokenCookie, refreshTokenCookie);
    }
}
```

---

### RateLimitingService

```java
@Service
@RequiredArgsConstructor
public class RateLimitingService {
    
    private final RateLimitingRepository rateLimitingRepository;
    
    public void checkRateLimit(String phoneNumber, DeviceInfo deviceInfo) {
        // 전화번호 체크
        checkPhoneRateLimit(phoneNumber);
        
        // Device ID 체크
        checkDeviceRateLimit(deviceInfo.getDeviceId());
        
        // IP 체크 (IP만 사용)
        checkIpRateLimit(deviceInfo.getClientIp());
    }
    
    private void checkPhoneRateLimit(String phoneNumber) {
        String key = "rate_limit:login:phone:" + phoneNumber;
        Long count = rateLimitingRepository.incrementAndGet(key, 3600);
        
        if (count > 5) {
            throw new RateLimitExceededException("1시간에 5번만 로그인 가능합니다");
        }
    }
    
    // Device ID, IP 체크도 동일한 패턴
}
```

---

## 7.3 Redis vs 로컬 캐시 교체

### Repository 인터페이스

```java
public interface RateLimitingRepository {
    Long incrementAndGet(String key, long ttlSeconds);
    void delete(String key);
    Long getTtl(String key);
}
```

---

### Redis 구현체

```java
@Repository
@RequiredArgsConstructor
public class RateLimitingRepositoryUsingRedis implements RateLimitingRepository {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Long incrementAndGet(String key, long ttlSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        
        return count;
    }
}
```

---

### 로컬 캐시 구현체

```java
@Repository
public class RateLimitingRepositoryUsingLocalCache implements RateLimitingRepository {
    
    private final Cache<String, Long> cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .build();
    
    @Override
    public Long incrementAndGet(String key, long ttlSeconds) {
        return cache.asMap().compute(key, (k, v) -> v == null ? 1L : v + 1);
    }
}
```

---

### 의존성 교체

**build.gradle.kts:**
```kotlin
dependencies {
    // Redis 사용 시
    implementation(project(":auth:auth-rate-limit:rate-limit-repository-using-redis"))
    
    // 로컬 캐시 사용 시
    // implementation(project(":auth:auth-rate-limit:rate-limit-repository-using-local-cache"))
}
```

**Service 코드는 변경 없음!** ✅

---

---

## 7.4 Redis 실행 및 테스트

### Docker로 Redis 실행

**1. Redis 실행:**
```bash
cd week2
docker-compose up -d
```

**2. Redis 실행 확인:**
```bash
docker ps
# redis 컨테이너가 실행 중인지 확인
```

**3. Redis CLI 접속:**
```bash
docker exec -it week2-redis-1 redis-cli
# 또는
redis-cli -h localhost -p 6379
```

---

### Redis 명령어 가이드

**Rate Limit 키 확인:**

```bash
# 모든 Rate Limit 키 조회
KEYS rate_limit:login:*

# 특정 전화번호의 Rate Limit 키 조회
KEYS rate_limit:login:phone:*

# 키의 남은 TTL 확인 (초 단위)
TTL rate_limit:login:phone:01012345678

# 키 삭제
DEL rate_limit:login:phone:01012345678
```

**예시 출력:**
```
127.0.0.1:6379> GET rate_limit:login:phone:01012345678
"3"
127.0.0.1:6379> TTL rate_limit:login:phone:01012345678
(integer) 3542
```

---

### 테스트 시나리오

**1. Redis 실행:**
```bash
cd week2
docker-compose up -d
```

**2. 애플리케이션 실행:**
```bash
./gradlew :lecture-service:bootRun
```

**3. 테스트 유저 생성:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "name": "테스트 유저",
    "password": "password123",
    "phoneNumber": "01012345678"
  }'
```

**4. 로그인 실패 테스트 (Rate Limit 확인):**
```bash
# 잘못된 비밀번호로 5번 시도
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "email": "test@example.com",
      "password": "wrongpassword"
    }'
  echo ""
done
```

**5. Redis에서 Rate Limit 키 확인:**
```bash
redis-cli -h localhost -p 6379

# Redis CLI에서 실행:
KEYS rate_limit:login:*
GET rate_limit:login:phone:01012345678
TTL rate_limit:login:phone:01012345678
```

**6. 로그인 성공 테스트:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**7. 로그인 성공 후 Rate Limit 리셋 확인:**
```bash
redis-cli -h localhost -p 6379

# Redis CLI에서 실행:
KEYS rate_limit:login:*
# 키가 삭제되었는지 확인 (결과가 없어야 함)
```

---

### Swagger UI로 테스트

**1. Swagger UI 접속:**
```
http://localhost:8080/swagger-ui.html
```

**2. User API에서 `POST /api/users` 호출:**
- Request Body:
```json
{
  "email": "test@example.com",
  "name": "테스트 유저",
  "password": "password123",
  "phoneNumber": "01012345678"
}
```

**3. Auth API에서 `POST /api/auth/login` 호출:**
- Request Body:
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```

**4. Response Headers에서 쿠키 확인:**
- `Set-Cookie: access_token=...`
- `Set-Cookie: refresh_token=...`

---

## 정리

✅ **전체 흐름을 이해하면 각 컴포넌트의 역할이 명확해진다**

✅ **Interceptor → ArgumentResolver → Controller → Orchestrator → Service 순서로 처리된다**

✅ **Repository 인터페이스로 Redis와 로컬 캐시를 교체할 수 있다**

✅ **Service 코드는 변경하지 않고도 기술 스택을 교체할 수 있다**

✅ **로그인 성공 시 Rate Limit 카운트가 자동으로 리셋된다**
