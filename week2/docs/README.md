# 옆집개발자 스터디 2주차 자료

## 스터디 개요

- **주제**: 무분별한 로그인 시도 방지를 통한 보안 강화 및 Redis 활용
- **대상**: 취업 준비생 (중급 수준)
- **시간**: 2시간
- **예제 프로젝트**: 강의 사이트 (Auth 모듈 확장)

## 스터디 목차

0. [스터디 전 질문들](./00-questions.md) - 스터디 전에 생각해볼 질문들
1. [도입: 주제 선정 이유](./01-introduction.md) (10분)
2. [토큰을 쿠키로 전송하기](./02-token-to-cookie.md) (25분)
3. [캐시로 해결할 수 있는 문제들](./03-problem-scenarios.md) (10분)
4. [Rate Limiting 해결 과정](./04-rate-limiting-solution.md) (25분)
5. [사용자 식별](./05-user-identification.md) (15분)
6. [Device ID와 IP 제어](./06-device-id-and-ip.md) (15분)
7. [전체 구현](./07-implementation.md) (15분)
8. [이력서 작성 가이드](./08-resume-writing.md) (10분)
9. [정리 및 Q&A](./09-summary.md) (5분)

## 프로젝트 구조

```
week2/
├── lecture-service/              # Main 모듈 (Spring Boot Application)
│
├── user/                        # User 도메인 모듈 (week1 참조)
│
└── auth/                        # Auth 도메인 모듈 (week1 확장)
    ├── auth-domain/             # 도메인 모델
    ├── auth-api/                # API 인터페이스 (DTO)
    ├── auth-controller/         # Controller 구현
    ├── auth-orchestrator/       # Orchestrator (DTO 조립)
    ├── auth-service/            # Service (비즈니스 로직)
    ├── auth-rate-limit/         # Rate Limiting 모듈 (신규)
    │   ├── rate-limit-service/  # Rate Limiting Service
    │   ├── rate-limit-repository/  # Repository 인터페이스
    │   ├── rate-limit-repository-using-redis/  # Redis 구현체
    │   └── rate-limit-repository-using-local-cache/  # 로컬 캐시 구현체
    └── auth-infrastructure/     # Infrastructure (Interceptor, ArgumentResolver)
```

## 핵심 메시지

1. **토큰은 쿠키에 저장하여 XSS 공격을 방지한다**
2. **문제 해결 과정을 단계별로 생각하는 것이 중요하다**
3. **캐시의 특성(TTL, 빠른 읽기/쓰기)을 활용해서 문제를 해결한다**
4. **Redis Rate Limiting으로 무분별한 로그인 시도를 방지한다**
5. **Device ID와 IP를 함께 사용하여 다층 방어를 구축한다**
6. **결국 중요한 건 '구현 기술'보다 '어떤 문제를 풀 것인가'를 정의하는 힘이다**

## 실행 방법

### 1. Redis 실행

```bash
cd week2
docker-compose up -d
```

### 2. 애플리케이션 실행

```bash
./gradlew :lecture-service:bootRun
```

### 3. 테스트

자세한 테스트 방법은 [07-implementation.md](./docs/07-implementation.md)의 "7.4 Redis 실행 및 테스트" 섹션을 참고하세요.

**간단한 테스트:**
1. Swagger UI 접속: http://localhost:8080/swagger-ui.html
2. `POST /api/users`로 테스트 유저 생성
3. `POST /api/auth/login`으로 로그인 테스트
4. Redis CLI로 Rate Limit 키 확인: `redis-cli -h localhost -p 6379`
