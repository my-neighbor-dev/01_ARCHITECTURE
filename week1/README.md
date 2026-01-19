# 강의 사이트 예제 프로젝트

이 프로젝트는 "옆집개발자 스터디"를 위한 예제 프로젝트입니다.

## 프로젝트 개요

강의 사이트를 예제로 하여 다음 내용을 학습합니다:

1. **Service는 비즈니스 로직만 담당**: 기술에 의존하지 않도록 설계
2. **Repository 인터페이스화**: 기술 스택 유연하게 교체하기
3. **Orchestrator 패턴**: Service 책임 분리 및 DTO 조립
4. **API 인터페이스 계층**: API 스펙을 코드로 명확하게 정의하기
5. **외부 API 호출**: Feign 패턴을 통한 기술 의존성 분리
6. **Entity와 도메인 모델 분리**: 도메인 모델이 기술에 독립적이게

## 프로젝트 구조

자세한 내용은 [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md)를 참고하세요.

## 스터디 자료

스터디 자료는 [docs/](./docs/) 폴더에 있습니다.

- [00-questions.md](./docs/00-questions.md) - **스터디 전에 생각해볼 질문들** ⭐
- [README.md](./docs/README.md) - 스터디 자료 목차
- [01-introduction.md](./docs/01-introduction.md) - 도입
- [02-problem.md](docs/02-service.md) - 문제 상황 파악
- [03-repository-interface.md](./docs/03-repository-interface.md) - Repository 인터페이스화
- [04-orchestrator.md](./docs/04-orchestrator.md) - Orchestrator 패턴
- [05-api-interface.md](./docs/05-api-interface.md) - API 인터페이스 계층
- [06-feign-external-api.md](./docs/06-feign-external-api.md) - 외부 API 호출: Feign 패턴
- [07-summary.md](./docs/07-summary.md) - 정리 및 Q&A

## 실행 방법

### 1. 프로젝트 빌드

```bash
./gradlew build
```

### 2. lecture-service 실행

```bash
./gradlew :lecture-service:bootRun
```

## 핵심 개념

### 1. Service는 비즈니스 로직만 담당

**목적**: Service가 기술에 의존하지 않도록 하기

**방법**:
- Service는 Repository 인터페이스만 참조 (구현체 직접 참조 안 함)
- Service는 도메인 모델만 사용 (Entity 직접 사용 안 함)
- 기술적인 부분은 Repository 구현체에만 존재

### 2. Repository 인터페이스화

**목적**: 기술 스택을 유연하게 교체하기

**방법**:
- 인터페이스는 도메인 모듈에 위치 (`user-repository`)
- 구현체는 별도 모듈에 위치 (`user-repository-using-jpa`, `user-repository-using-jdbc`)
- Service는 인터페이스만 참조

### 3. Orchestrator 패턴

**목적**: Service 책임 분리 및 DTO 조립

**방법**:
- Service는 비즈니스 로직만 담당
- Orchestrator가 여러 Service를 조율하여 DTO 조립
- Controller는 Orchestrator만 호출

### 4. API 인터페이스 계층

**목적**: API 스펙을 코드로 명확하게 정의하기

**방법**:
- API 인터페이스에 Swagger 어노테이션 추가
- Controller가 API 인터페이스를 구현
- 다른 서비스에서 API 인터페이스만 의존하면 API 스펙을 알 수 있음

### 5. 외부 API 호출: Feign 패턴

**목적**: Service가 기술(Feign Client)에 의존하지 않도록 하기

**방법**:
- 외부 API 인터페이스 생성 (auth-external-api)
- Feign 구현체 생성 (auth-external-api-using-feign)
- Service는 인터페이스만 참조 (Feign 구현체 직접 참조 안 함)

### 6. Entity와 도메인 모델 분리

**목적**: 도메인 모델이 기술에 독립적이게 하기

**방법**:
- 도메인 모델은 domain 모듈에 위치 (기술 독립적)
- Entity는 Repository 구현체에만 위치 (JPA 의존적)
- Repository 구현체에서 Entity → Domain 변환

## 라이선스

이 프로젝트는 스터디 목적으로 작성되었습니다.
