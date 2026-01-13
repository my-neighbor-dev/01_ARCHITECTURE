# 옆집개발자 스터디 1주차 자료

## 스터디 개요

- **주제**: Service는 비즈니스 로직만 담당하고 기술에 의존하지 않도록 하는 모듈화 전략
- **대상**: 취업 준비생 (중급 수준)
- **시간**: 2시간
- **예제 프로젝트**: 강의 사이트 (User, Auth 모듈)

## 스터디 목차

0. [스터디 전 질문들](./00-questions.md) - 스터디 전에 생각해볼 질문들
1. [도입: 질문으로 시작하기](./01-introduction.md) (5분)
2. [뚱뚱한 Service](02-service.md) (20분)
3. [Repository 인터페이스화](./03-repository-interface.md) (20분)
4. [Orchestrator 패턴 도입](./04-orchestrator.md) (25분)
5. [API 인터페이스 계층](./05-api-interface.md) (15분)
6. [외부 API 호출: Feign 패턴](./06-feign-external-api.md) (20분)
7. [정리 및 Q&A](./07-summary.md) (10분)

## 프로젝트 구조

```
lecture-service/
├── lecture-service/              # Main 모듈 (Spring Boot Application)
│
├── user/                        # User 도메인 모듈
│   ├── user-domain/             # 도메인 모델 (User)
│   ├── user-api/                # API 인터페이스 (DTO)
│   ├── user-controller/         # Controller 구현
│   ├── user-orchestrator/       # Orchestrator (DTO 조립)
│   ├── user-service/            # Service 구현체
│   ├── user-repository/         # Repository 인터페이스
│   ├── user-repository-using-jpa/  # JPA 구현체
│   └── user-repository-using-jdbc/ # JDBC 구현체 (교체 예시)
│
└── auth/                        # Auth 도메인 모듈
    ├── auth-domain/             # 도메인 모델 (AuthUser, AuthToken, LoginResult)
    ├── auth-api/                # API 인터페이스 (DTO)
    ├── auth-controller/         # Controller 구현
    ├── auth-orchestrator/       # Orchestrator (DTO 조립)
    ├── auth-service/            # Service (비즈니스 로직)
    ├── auth-repository/         # Repository 인터페이스
    ├── auth-repository-using-jpa/  # JPA 구현체
    ├── auth-external-api/       # 외부 API 인터페이스 (AuthUserApi)
    └── auth-external-api-using-feign/  # Feign 구현체
```

## 핵심 메시지

1. **Service는 비즈니스 로직만 담당하고 기술에 의존하지 않아야 한다**
2. **Repository 인터페이스화로 기술 스택을 유연하게 교체할 수 있다**
3. **Orchestrator 패턴으로 Service를 얇게 유지하고 책임을 분리한다**
4. **Service는 비즈니스 로직, Orchestrator는 DTO 조립만 담당한다**
5. **외부 모듈 호출은 인터페이스 + Feign 패턴을 사용하여 기술 의존성을 분리한다**
6. **Entity와 도메인 모델을 분리하여 도메인 모델이 기술에 독립적이게 한다**
7. **결국 중요한 건 '구현 기술'보다 '어떤 문제를 풀 것인가'를 정의하는 힘이다**
