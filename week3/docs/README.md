# 옆집개발자 스터디 3주차 자료

## 스터디 개요

- **주제**: 반복되는 권한 체크, AOP로 우아하게 해결하기 (Data Ownership & AOP)
- **대상**: 취업 준비생 (중급 수준)
- **시간**: 2시간
- **예제 프로젝트**: 강의 사이트 (인가 모듈 확장)

## 스터디 목차

0. [스터디 전 질문들](./00-questions.md) - 스터디 전에 생각해볼 질문들
2. [문제 상황: 데이터 소유권 체크 필요성](./02-problem-scenario.md) (15분)
3. [시도 1, 2: 단순한 접근의 한계](./03-naive-approach.md) (20분)
4. [AOP + 전략 패턴으로 해결](./04-aop-solution.md) (20분)
5. [구현 단계별 설명](./05-implementation.md) (25분)
6. [실제 사용 예시](./06-examples.md) (15분)
7. [이력서 작성 가이드](./07-resume-writing.md) (10분)
8. [정리 및 Q&A](./08-summary.md) (5분)

## 프로젝트 구조

```
week3/
├── lecture-service/              # Main 모듈 (Spring Boot Application)
│
├── user/                        # User 도메인 모듈 (week1, week2 참조)
│
├── auth/                        # Auth 도메인 모듈 (week1, week2 참조)
│
├── authorization/               # 인가 모듈 (신규)
│   ├── authorization-common/    # 공통 인터페이스 (ResourceOwnership, DomainFinder, DataPermissionCheckType, UserInfo)
│   ├── authorization-annotation/  # Annotation (@CheckDataPermission, @PermissionId, 도메인별 Annotation)
│   └── authorization-aspect/    # Aspect 구현 (DataPermissionAspect, UserInfoArgumentResolver, UserInfoInterceptor)
│
├── lecture/                     # Lecture 도메인 모듈 (신규)
│   ├── lecture-domain/          # 도메인 모델 (Lecture - ResourceOwnership 구현)
│   ├── lecture-api/             # API 인터페이스 (DTO)
│   ├── lecture-controller/      # Controller 구현
│   ├── lecture-orchestrator/    # Orchestrator (DTO 조립)
│   ├── lecture-service/         # Service (비즈니스 로직, LectureSearchService)
│   ├── lecture-repository/      # Repository 인터페이스
│   └── lecture-repository-using-jpa/  # JPA 구현체
│
└── group/                       # Group 도메인 모듈 (신규)
    ├── group-domain/            # 도메인 모델 (Group - ResourceOwnership 구현, UserGroupMapping)
    ├── group-api/               # API 인터페이스 (DTO)
    ├── group-controller/        # Controller 구현
    ├── group-orchestrator/      # Orchestrator (DTO 조립)
    ├── group-service/           # Service (비즈니스 로직, GroupSearchService, UserGroupMappingService)
    ├── group-repository/        # Repository 인터페이스
    └── group-repository-using-jpa/  # JPA 구현체
```

## 핵심 메시지

1. **Role 기반 인가는 "무엇을 할 수 있는가"만 체크하고, "누구의 데이터인가"는 체크하지 못한다**
2. **데이터 소유권 체크는 반복되는 보안 로직이지만, 비즈니스 로직과 분리되어야 한다**
3. **AOP를 활용하면 비즈니스 로직을 건드리지 않고 보안 로직을 추가할 수 있다**
4. **전략 패턴으로 도메인별 다른 검증 로직을 유연하게 처리할 수 있다**
5. **결국 중요한 건 '구현 기술'보다 '어떤 문제를 풀 것인가'를 정의하는 힘이다**

## 실행 방법

### 1. 애플리케이션 실행

```bash
cd week3
./gradlew :lecture-service:bootRun
```

### 2. 테스트

자세한 테스트 방법은 [06-examples.md](./06-examples.md)를 참고하세요.

**간단한 테스트:**
1. Swagger UI 접속: http://localhost:8080/swagger-ui.html
2. Header에 `X-User-Id` 설정 (예: 1)
3. `POST /api/lectures`로 강의 생성
4. `GET /api/lectures/{lectureId}`로 강의 조회 (자신이 만든 강의만 조회 가능)
5. `POST /api/groups`로 그룹 생성
6. `POST /api/groups/{groupId}/users`로 유저를 그룹에 매핑
7. `GET /api/groups/{groupId}`로 그룹 조회 (자신이 속한 그룹만 조회 가능)
