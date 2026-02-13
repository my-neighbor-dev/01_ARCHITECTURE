---
trigger: glob
description: Backend Common Patterns (Transactional, Events, Concurrency)
globs: backend/**/*.kt
---

# Backend Common Patterns

## 1. TransactionalExecutor

Spring의 `@Transactional` 어노테이션 사용을 지양하고, 명시적인 **TransactionalExecutor**를 사용합니다.

-   **Why?**: AOP 기반 트랜잭션의 숨겨진 동작(Proxy)을 제거하고, 트랜잭션 범위를 코드 레벨에서 명확히 하기 위함.
-   **Rule**:
    *   Repository(JPA) 내부에서는 `@Transactional` 사용 가능.
    *   Orchestrator/Service 레벨에서는 반드시 `transactionalExecutor.execute { }` 블록 사용.

```kotlin
// ✅ Good
fun doSomething() {
    transactionalExecutor.execute {
        // Business Logic
    }
}

// ❌ Bad
@Transactional
fun doSomething() { ... }
```

## 2. Event Publishing

도메인 이벤트 발행은 **Orchestrator** 계층에서 수행하는 것을 원칙으로 합니다.

-   **Rule**:
    *   Service는 순수 로직만 수행하고 이벤트를 반환하거나 상태만 변경.
    *   Orchestrator가 Service 호출 후 결과에 따라 `ApplicationEventPublisher`를 통해 이벤트 발행.
    *   Domain Model 내부에서 이벤트 발행 로직 포함 금지.

## 3. Domain Lock (Concurrency)

동시성 이슈가 민감한 도메인(예: Issue 상태 변경)은 명시적인 Lock을 사용합니다.

-   **Pattern**: `{Domain}Lock` (예: `IssueLock`)
-   **Usage**: 상태 변경이나 중요한 업데이트 시 반드시 Lock 획득 후 수행.

```kotlin
issueLock.withIssueLock(issueId) {
    transactionalExecutor.execute {
        // Safe Update
    }
}
```
