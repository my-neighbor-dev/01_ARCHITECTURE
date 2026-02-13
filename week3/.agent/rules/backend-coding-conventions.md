---
trigger: glob
description: Backend Coding Conventions (Naming, Purity, JSON)
globs: backend/**/*.kt
---

# Backend Coding Conventions

## 🚨 Essential: Verification First

백엔드 코드 작성 전, 반드시 다음 설정 파일들을 숙지하여 린트 및 아키텍처 위반을 예방해야 합니다.

1.  **Detekt Rules**: `backend/detekt.yml`의 규칙을 준수하세요. (ComplexMethod, LongParameterList 등)
2.  **Architecture Tests**: `backend/application/issue-tracking-app/src/test/kotlin/com/lgvoltup/issuetracking/architecture/ArchitectureTest.kt`를 확인하여 계층 간 의존성 규칙을 상세히 숙지하세요.
    -   *작업 전 `./gradlew devCheck`를 실행하여 현재 상태를 확인하는 것을 권장합니다.*

---

## 1. Semantic Naming

JPA 기술 용어(`find`, `save`, `delete`) 대신 **비즈니스 의도**가 담긴 용어를 사용합니다.

| Ban | Recommended |
| :--- | :--- |
| `find*` | `retrieve`, `search`, `load`, `get` |
| `save*` | `register`, `create`, `update`, `store` |
| `delete*` | `remove`, `withdraw`, `cancel` |

## 2. Service Naming

-   **Pattern**: `{Action}{Domain}Service`
-   **Example**: `RegisterIssueService`, `AssignChargerService`
-   **Rule**: 하나의 Service 클래스는 하나의 책임(Action)만 가집니다.

## 3. Domain Model Purity

Domain Model은 프레임워크로부터 순수해야 합니다.

-   **❌ Ban**:
    *   Spring Dependency (`@Service`, `@Component` 등)
    *   JPA Annotations (`@Entity`, `@Table` in Domain Model) -> Entity는 Infrastructure에 존재.
    *   Jackson Annotations (`@JsonProperty`)
-   **✅ Allow**:
    *   Pure Kotlin Classes/Data Classes.
    *   `.kotlin_builtins` types.

## 4. Date & Time

-   **Rule**: 모든 날짜/시간은 **`ZonedDateTime`**을 사용합니다.
-   **❌ Ban**: `Date`, `Calendar`, `LocalDateTime`(Timezone 모호함), `String` 날짜.

## 5. JSON Handling

-   Domain Model 내부에서 JSON String 변환을 수행하지 않습니다.
-   변환이 필요하다면 **Infrastructure(Entity)** 계층에서 `JsonUtils` 등을 활용해 변환합니다.
