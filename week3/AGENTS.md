# Agent Guidelines

이 문서는 AI Agent가 프로젝트 작업 시 **반드시 준수해야 할 핵심 규칙**을 정의합니다.

## 1. 공통 규칙 (Global)
- **언어**: 모든 응답, 문서, 주석, 커밋 메시지는 **한국어**로 작성합니다.
- **문서 확인**: `README.md`, `GEMINI.md`, `AGENTS.md` 필수 확인.

## 2. Backend 개발 규칙
> 📘 **[상세 정책 문서](docs/policies/backend/BACKEND_DEVELOPMENT_POLICY.md)**

- **아키텍처**: `Presentation` → `Orchestrator` → `DomainService` → `DomainModel` ← `RepositoryAdapter`
- **Domain Model**: 순수 Kotlin (No Spring/JPA/Jackson).
- **트랜잭션**: `@Transactional` 금지 → `transactionalExecutor` 사용.
- **네이밍**: `find*` 금지 → `retrieve*` 등 Semantic Naming 사용.

## 3. Frontend 개발 규칙 (Vite + Clean Architecture)
> 📘 **[상세 정책 문서](docs/policies/frontend/FRONTEND_DEVELOPMENT_POLICY.md)**

### 아키텍처 & 의존성
- **계층**: `Presentation` → `Application` → `Core` ← `Infrastructure`
- **Facade 패턴 필수**:
  - React Query → `useQuery` 직접 금지. **`@/application/queries`** 사용.
  - Forms → `useForm` 직접 금지. **`@/presentation/hooks/use-form`** 사용.
  - API call → `axios` 직접 금지. **`@/infrastructure`** 구현체 사용.

### 기술 스택 & 코드 스타일
- **Stack**: Vite, React 18, TypeScript, Tailwind CSS v4, Radix UI.
- **Naming**: 파일명은 **`kebab-case`** 필수.
- **Linting**: `any` 타입 금지, `console.log` 금지, Architecture Rule 준수.

## 4. 검증 (Verification)

### Backend
```bash
./scripts/check-backend.sh
```

### Frontend
```bash
./scripts/check-frontend.sh
```
