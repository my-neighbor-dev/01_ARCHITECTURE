# 6. Device ID와 IP로 다층 방어

## 질문

> **"한 명의 공격자가 여러 사용자를 공격하는 경우는 어떻게 막을까요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- 전화번호만으로는 막을 수 없는 경우는?
- Device ID와 IP 주소를 함께 사용하면 어떤 장점이 있을까?
- 여러 기준을 동시에 체크하는 것이 왜 더 안전할까?

---

## 6.1 문제: 전화번호만으로는 부족함

**시나리오:**
- 공격자가 여러 계정으로 로그인 시도
- 각 계정마다 전화번호가 다름
- 전화번호만으로는 막을 수 없음

**예시:**
```
공격자 A:
- 계정 1: 010-1111-1111 (5번 시도)
- 계정 2: 010-2222-2222 (5번 시도)
- 계정 3: 010-3333-3333 (5번 시도)
...
총 15번 이상 시도 가능!
```

**해결:**
- Device ID와 IP를 추가하여 다층 방어 구축

---

## 6.2 Device ID 기반 제한

### Device ID란?

**정의:**
- 사용자 장치를 식별하는 고유 ID
- 쿠키로 관리 (DeviceIdInterceptor에서 생성/관리)
- 사용자가 변경하기 어려움

**장점:**
- 같은 장치에서 여러 계정으로 공격해도 막을 수 있음
- 장치 기반 제한

**구현:**
```java
public void checkDeviceRateLimit(String deviceId) {
    String key = "rate_limit:login:device:" + deviceId;
    Long count = redis.increment(key);
    
    if (count == 1) {
        redis.expire(key, 3600); // 1시간
    }
    
    if (count > 5) {
        throw new RateLimitExceededException("1시간에 5번만 로그인 가능합니다");
    }
}
```

**제한:**
- Device ID당 1시간에 5번

---

## 6.3 IP 기반 제한

### IP 주소란?

**정의:**
- 요청을 보낸 클라이언트의 IP 주소
- 요청 헤더에서 추출 가능

**장점:**
- 같은 IP에서 여러 계정으로 공격해도 막을 수 있음
- 네트워크 기반 제한

**구현:**
```java
public void checkIpRateLimit(String ipAddress) {
    String key = "rate_limit:login:ip:" + ipAddress;
    Long count = redis.increment(key);
    
    if (count == 1) {
        redis.expire(key, 3600); // 1시간
    }
    
    if (count > 100) {  // IP는 더 여유있게 설정
        throw new RateLimitExceededException("1시간에 100번만 로그인 가능합니다");
    }
}
```

**제한:**
- IP당 1시간에 100번 (더 여유있게 설정)

**이유:**
- 공유 IP 환경 고려 (회사, 학교 등)
- 정상 사용자도 영향을 받을 수 있음

---

## 6.4 다층 방어 구축

### 동시 체크

**로직:**
- 전화번호 체크
- Device ID 체크
- IP 체크
- **둘 중 하나라도 초과하면 차단**

**구현:**
```java
public void checkRateLimit(DeviceInfo deviceInfo, String phoneNumber) {
    // 1. 전화번호 체크
    checkPhoneRateLimit(phoneNumber);
    
    // 2. Device ID 체크
    checkDeviceRateLimit(deviceInfo.getDeviceId());
    
    // 3. IP 체크 (IP만 사용)
    checkIpRateLimit(deviceInfo.getClientIp());
}
```

---

## 6.6 공격 시나리오별 방어

### 시나리오 1: 같은 장치, 다른 계정

**공격:**
- 같은 Device ID로 여러 계정 공격

**방어:**
- Device ID 체크로 차단 ✅

---

### 시나리오 2: 다른 장치, 같은 IP

**공격:**
- 같은 IP에서 여러 장치로 공격

**방어:**
- IP 체크로 차단 ✅

---

### 시나리오 3: 쿠키 삭제 후 재시도

**공격:**
- Device ID 쿠키 삭제 후 새 Device ID 생성

**방어:**
- IP 체크로 차단 ✅

---

## 정리

✅ **전화번호만으로는 여러 계정 공격을 막을 수 없다**

✅ **Device ID와 IP를 함께 사용하여 다층 방어를 구축한다**

✅ **여러 기준을 동시에 체크하여 더 강력한 보안을 제공한다**

✅ **IP는 공유 환경을 고려하여 더 여유있게 설정한다**
