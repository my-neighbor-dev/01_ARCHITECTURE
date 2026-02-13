# Backend 개발 정책

> 최종 업데이트: 2025-12-08  
> 버전: 2.0.0

이 문서는 Backend 개발의 핵심 원칙을 간결하게 정리합니다. 각 지침의 상세 설명은 링크된 문서를 참조하세요.

## 🏗️ 아키텍처 원칙

### 1. 레이어 아키텍처

```
Presentation → Orchestrator → DomainService → DomainModel ← RepositoryAdapter
```

- 의존성은 한 방향으로만 흐름
- Domain Model은 순수해야 하며 프레임워크에 의존하지 않음
- Repository Port는 Domain에, Adapter는 Infrastructure에 위치

→ **[상세 문서](backend/01-layer-architecture.md)**

### 2. TransactionalExecutor 필수

- ❌ `@Transactional` 어노테이션 사용 금지 (Repository 제외)
- ✅ `transactionalExecutor.execute { }` 사용

→ **[상세 문서](backend/02-transactional-executor.md)**

### 3. Event 발행은 Orchestrator에서만

- ❌ Service에서 `ApplicationEventPublisher` 사용 금지
- ✅ Orchestrator에서만 Event 발행

→ **[상세 문서](backend/03-event-publishing.md)**

## 📝 네이밍 규칙

### 4. Semantic Naming (의미론적 네이밍)

**금지**: `find*`, `save*`, `delete*` (JPA 기술 용어)  
**권장**: 비즈니스 의도를 명확히 표현하는 이름 (컨텍스트에 맞게 선택)

| 금지        | 권장 예시                                                        | 
|-----------|--------------------------------------------------------------|
| `find*`   | `retrieve*`, `get*`, `search*`, `query*`, `lookup*`          |
| `save*`   | `register*`, `create*`, `update*`, `record*`, `store*`       |
| `delete*` | `withdraw*`, `remove*`, `cancel*`, `deactivate*`, `archive*` |

**중요**: 일방적으로 하나로 강제되지 않습니다. 상황에 가장 적합한 이름을 선택하세요.

→ **[상세 문서](backend/04-semantic-naming.md)**

### 5. Service 네이밍 규칙

- 패턴: `{Action}{Domain}Service`
- 허용 액션: Get, Create, Update, Delete, Search, Register, Retrieve, Withdraw, Classify, Record, Assign, Change, Process, Validate, Verify, Send, Publish

→ **[상세 문서](backend/04-semantic-naming.md#service-naming)**

## 🎯 Domain Model 규칙

### 6. Domain Model 순수성

- ❌ Spring, JPA, Jackson 의존성 금지
- ✅ 순수한 Kotlin 타입만 사용 (Map, List, ZonedDateTime)
- ✅ Repository Port는 Spring Data 타입(Pageable, Page) 허용

→ **[상세 문서](backend/05-domain-model-purity.md)**

### 7. JSON 변환은 Infrastructure 계층에서만

- ❌ Domain Model에서 JSON String 사용 금지
- ❌ `ObjectMapper` 직접 사용 금지
- ✅ JPA Entity에서 `JsonUtils` 사용하여 변환

→ **[상세 문서](backend/06-json-conversion.md)**

### 8. 날짜는 항상 ZonedDateTime

- ❌ String 날짜 금지
- ✅ `ZonedDateTime` 사용

→ **[상세 문서](backend/05-domain-model-purity.md#date-handling)**

## 🔒 동시성 제어

### 9. {Domain}Lock 사용

- Issue 상태/값 변경 시 `IssueLock` 필수
- 패턴: `{Domain}Lock` (예: IssueLock, ChargerLock)

```kotlin
issueLock.withIssueLock(issueId) {
    transactionalExecutor.execute { /* 상태 변경 */ }
}
```

→ **[상세 문서](backend/07-domain-lock-usage.md)**

### 10. Enum 변환 안전성

- ❌ `valueOf` 직접 사용 금지 (런타임 예외 위험)
- ❌ Enum 대상 `when`에서 `else` 분기 금지
- ✅ 명시적 `when` 절로 모든 값 나열 (컴파일 타임 체크)
- ✅ String → Enum 변환 시 `fromName()` 패턴 사용

→ **[상세 문서](backend/08-enum-safety.md)**

## 📦 모듈별 역할

| 모듈                 | 핵심 규칙                                     |
|--------------------|-------------------------------------------|
| **api**            | 비즈니스 로직 없음, Swagger 문서화만                  |
| **domain-model**   | No JPA, No Spring, 순수 Kotlin              |
| **domain-service** | 하나의 Service = 하나의 책임                      |
| **orchestrator**   | TransactionalExecutor 사용, Domain Model 반환 |
| **presentation**   | Mapper로 DTO 변환, 비즈니스 로직 없음                |
| **repository**     | Entity ↔ Domain Model 변환                  |

→ **[상세 문서](backend/01-layer-architecture.md#module-roles)**

## ✅ 체크리스트

### 새로운 기능 개발 시

- [ ] Service 이름이 `{Action}{Domain}Service` 패턴인가?
- [ ] `find*`, `save*`, `delete*` 대신 `retrieve*`, `register*`, `withdraw*` 사용했는가?
- [ ] `@Transactional` 대신 `TransactionalExecutor` 사용했는가?
- [ ] Event 발행은 Orchestrator에서만 하는가?
- [ ] Domain Model에 Spring/JPA/Jackson 의존성이 없는가?
- [ ] JSON 변환은 Entity에서 `JsonUtils`로 하는가?
- [ ] Issue 상태 변경 시 `IssueLock` 사용했는가?

### 코드 리뷰 시

- [ ] 레이어 의존성 방향이 올바른가?
- [ ] Repository Port는 Service에서만 호출하는가?
- [ ] Orchestrator가 여러 Service를 조합하는가?
- [ ] Domain Model이 순수한가?

## 🔍 검증 도구

이 정책들은 다음 도구로 자동 검증됩니다:

- **ktlint**: 코드 스타일
- **detekt**: Semantic Naming, @Transactional, ApplicationEventPublisher 위치
- **ArchUnit**: 레이어 의존성, Domain Model 순수성, Repository 접근, Event 발행 위치

검증 실행:

```bash
cd backend
./gradlew devCheck  # ktlint + detekt + ArchUnit
```

→ **[검증 정책 상세](../VERIFICATION_POLICY.md)**

## 📚 추가 자료

- [전체 아키텍처 문서](../PROJECT_ARCHITECTURE.md)
- [Backend 코딩 가이드](../developGuide/BACKEND_CODING_GUIDE.md)
- [이벤트 기반 아키텍처 가이드](../developGuide/EVENT_DRIVEN_ARCHITECTURE_GUIDE.md)

