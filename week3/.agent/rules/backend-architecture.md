---
trigger: glob
description: Backend Architecture Guide (Layers, Modules, Dependencies)
globs: backend/**/*.kt, backend/**/*.kts
---

# Backend Architecture Rules

## 1. Layered Architecture (Hexagonal / DDD)

프로젝트는 엄격한 **Layered Architecture**를 따릅니다. 의존성은 오직 **안쪽(Domain)**으로만 향해야 합니다.

```
[Presentation] ➡️ [Orchestrator] ➡️ [Domain Service] ➡️ [Domain Model] ⬅️ [Repository Adapter]
```

### 계층별 책임 및 규칙

| 계층 (Layer) | 모듈명 패턴 | 책임 (Responsibility) | 규칙 (Rules) |
| :--- | :--- | :--- | :--- |
| **Presentation** | `*-presentation` | 외부 요청 처리, DTO 변환 | **비즈니스 로직 금지.** 오직 Orchestrator만 호출 가능. |
| **Orchestrator** | `*-orchestrator` | 트랜잭션 관리, 유스케이스 조정 | **도메인 로직 직접 구현 금지.** Service를 조합하여 Flow 제어. |
| **Domain Service** | `*-domain-service` | 순수 비즈니스 로직 | **Infrastructure 의존 금지.** 하나의 Service는 하나의 책임만 가짐. |
| **Domain Model** | `*-domain-model` | 핵심 도메인 모델 & Port | **Spring/JPA 의존성 절대 금지.** 순수 Kotlin 클래스여야 함. |
| **Repository Adapter** | `*-repository-*` | 영속성 처리 (DB 구현) | **Domain Model의 Port 구현.** Entity ↔ Domain Model 변환 담당. |

## 2. Dependency Rules

1.  **Direct Dependency**: 상위 계층은 하위 계층을 직접 참조할 수 있습니다. (예: Orchestrator → Domain Service)
2.  **Inversion of Control (DIP)**: Repository는 Domain Model에 정의된 **Port(Interface)**를 구현해야 합니다.
    *   Application(Service)은 Repository의 구현체를 알지 못하며, Interface에만 의존합니다.
3.  **No skip**: 계층을 건너뛰는 호출은 원칙적으로 금지합니다. (예: Presentation → Domain Service 직접 호출 지양, Orchestrator 경유 권장)

## 3. Module Roles

-   **api**: Swagger 등 API 명세만 포함. 로직 없음.
-   **external-api**: 외부 시스템(Feign 등) 통신 담당.
-   **internal-api**: 모듈 간 통신을 위한 내부 API 명세.
