# 프로젝트 구조 설명

## 전체 모듈 구조

```
lecture-service/
├── lecture-service/              # Main 모듈 (Spring Boot Application)
│   ├── src/main/java/com/lecture/LectureServiceApplication.java
│   └── build.gradle.kts
│
├── user/                        # User 도메인 모듈
│   ├── user-domain/             # 도메인 모델
│   ├── user-api/                # API 인터페이스 (DTO, Request/Response)
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

## 모듈별 역할

### User 모듈

#### user-domain
- **역할**: 도메인 모델 정의
- **포함**: `User.java`
- **의존성**: 없음

#### user-api
- **역할**: API 인터페이스 및 DTO 정의
- **포함**: 
  - `UserApi.java` (인터페이스, Swagger 어노테이션 포함)
  - `UserResponse.java` (일반 조회용 DTO)
  - `AuthUserResponse.java` (인증용 DTO, password 포함)
- **의존성**: 없음 (순수 DTO)

#### user-controller
- **역할**: HTTP 요청/응답 처리 (API 인터페이스 구현)
- **포함**: `UserController.java` (UserApi 인터페이스 구현)
- **의존성**: `user-api`, `user-orchestrator`

#### user-orchestrator
- **역할**: DTO 조립 (Domain → DTO 변환)
- **포함**: `UserOrchestrator.java`
- **의존성**: `user-api`, `user-service`, `user-domain`

#### user-service
- **역할**: Service 구현체 (비즈니스 로직)
- **포함**: `UserService.java` (구현체)
- **의존성**: `user-domain`, `user-repository`

#### user-repository
- **역할**: Repository 인터페이스 정의
- **포함**: `UserRepository.java` (인터페이스)
- **의존성**: `user-domain`

#### user-repository-using-jpa
- **역할**: JPA를 사용하는 Repository 구현체
- **포함**: `UserRepositoryUsingJpa.java`, `UserEntity.java`, `UserJpaRepository.java`
- **의존성**: `user-repository`

#### user-repository-using-jdbc
- **역할**: JDBC를 사용하는 Repository 구현체 (교체 예시)
- **포함**: `UserRepositoryUsingJdbc.java`
- **의존성**: `user-repository`

### Auth 모듈

#### auth-domain
- **역할**: 도메인 모델 정의
- **포함**: `AuthUser.java`, `AuthToken.java`, `LoginResult.java`
- **의존성**: 없음

#### auth-api
- **역할**: API 인터페이스 및 DTO 정의
- **포함**: 
  - `AuthApi.java` (인터페이스, Swagger 어노테이션 포함)
  - `LoginRequest.java`, `LoginResponse.java` (DTO)
- **의존성**: 없음 (순수 DTO)

#### auth-controller
- **역할**: HTTP 요청/응답 처리 (API 인터페이스 구현)
- **포함**: `AuthController.java` (AuthApi 인터페이스 구현)
- **의존성**: `auth-api`, `auth-orchestrator`

#### auth-orchestrator
- **역할**: DTO 조립 (여러 Service 조율하여 DTO 생성)
- **포함**: `AuthOrchestrator.java`
- **의존성**: `auth-api`, `auth-domain`, `auth-service`

#### auth-service
- **역할**: Service 구현체 (비즈니스 로직 처리)
- **포함**: 
  - `AuthService.java` (로그인 비즈니스 로직: 유저 조회, 비밀번호 검증, 토큰 생성)
  - `CookieService.java` (쿠키 생성)
- **의존성**: `auth-api`, `auth-domain`, `auth-external-api`

#### auth-repository
- **역할**: Repository 인터페이스 정의
- **포함**: `AuthRepository.java` (인터페이스)
- **의존성**: 없음

#### auth-repository-using-jpa
- **역할**: JPA를 사용하는 Repository 구현체
- **포함**: `AuthRepositoryUsingJpa.java`, `AuthTokenEntity.java`, `AuthTokenJpaRepository.java`
- **의존성**: `auth-repository`

#### auth-external-api
- **역할**: 외부 API 인터페이스 (User 모듈 API 호출용)
- **포함**: `AuthUserApi.java` (인터페이스)
- **의존성**: `auth-domain`

#### auth-external-api-using-feign
- **역할**: Feign 구현체 (User 모듈 API 호출)
- **포함**: 
  - `AuthUserApiFeign.java` (AuthUserApi 구현체)
  - `UserApiFeignClient` (Feign Client 인터페이스)
- **의존성**: `auth-external-api`, `auth-domain`, `user-api`

## 계층 구조

### 올바른 참조 순서

```
API (인터페이스)
  ↓
Controller (구현)
  ↓
Orchestrator (DTO 조립)
  ↓
Service (비즈니스 로직)
  ↓
Repository (인터페이스)
  ↓
Repository 구현체 (JPA/JDBC 등)
```

### User 모듈 예시

```
UserApi (인터페이스)
  ↓
UserController → UserOrchestrator → UserService → UserRepository → UserRepositoryUsingJpa
```

### Auth 모듈 예시

```
AuthApi (인터페이스)
  ↓
AuthController → AuthOrchestrator → AuthService → AuthUserApi (인터페이스)
                                                      ↓
                                              AuthUserApiFeign → UserApiFeignClient (Feign)
                                                                      ↓
                                                              UserApi (실제 API 호출)
```

## 의존성 방향

### 핵심 원칙

1. **도메인 모델은 domain 모듈에 위치**
   - `user-domain`: `User`
   - `auth-domain`: `AuthUser`, `AuthToken`, `LoginResult`

2. **DTO는 API 모듈에 위치 (도메인 참조 없음)**
   - `user-api`: `UserResponse`, `AuthUserResponse`
   - `auth-api`: `LoginRequest`, `LoginResponse`

3. **Orchestrator에서 DTO 변환**
   - Domain → DTO 변환은 Orchestrator에서 처리
   - DTO는 도메인을 참조하지 않음

4. **외부 모듈 호출은 인터페이스 + Feign**
   - `AuthUserApi` (인터페이스) → `AuthUserApiFeign` (Feign 구현체)
   - Feign 구현체만 Feign 설정 포함

5. **Service는 비즈니스 로직만 담당**
   - `AuthService`: 유저 조회, 비밀번호 검증, 토큰 생성
   - `Orchestrator`: DTO 조립만 담당

6. **인터페이스화 기준**
   - **같은 모듈 내의 Service**: 구현체 직접 참조 (인터페이스 불필요)
   - **다른 모듈의 Service**: 인터페이스 참조 (AuthUserApi)
   - **Repository**: 항상 인터페이스 (기술 스택 교체 가능)

## 주요 파일 위치

### User 모듈
- `user-domain/User.java` - 도메인 모델
- `user-api/UserApi.java` - API 인터페이스
- `user-api/UserResponse.java` - 일반 조회용 DTO
- `user-api/AuthUserResponse.java` - 인증용 DTO (password 포함)
- `user-controller/UserController.java` - Controller
- `user-orchestrator/UserOrchestrator.java` - Orchestrator
- `user-service/UserService.java` - Service 구현체
- `user-repository/UserRepository.java` - Repository 인터페이스
- `user-repository-using-jpa/UserRepositoryUsingJpa.java` - JPA 구현체

### Auth 모듈
- `auth-domain/AuthUser.java` - 도메인 모델
- `auth-domain/AuthToken.java` - 도메인 모델
- `auth-domain/LoginResult.java` - 도메인 모델
- `auth-api/AuthApi.java` - API 인터페이스
- `auth-api/LoginRequest.java` - DTO
- `auth-api/LoginResponse.java` - DTO
- `auth-controller/AuthController.java` - Controller
- `auth-orchestrator/AuthOrchestrator.java` - Orchestrator
- `auth-service/AuthService.java` - Service (비즈니스 로직)
- `auth-service/CookieService.java` - Service
- `auth-external-api/AuthUserApi.java` - 외부 API 인터페이스
- `auth-external-api-using-feign/AuthUserApiFeign.java` - Feign 구현체

## 실행 방법

1. 프로젝트 루트에서 빌드:
```bash
./gradlew build
```

2. lecture-service 실행:
```bash
./gradlew :lecture-service:bootRun
```
