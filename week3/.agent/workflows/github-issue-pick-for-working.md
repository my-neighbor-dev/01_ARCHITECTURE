---
description: GitHub Issue 기반 작업 시작 (브랜치 생성 포함)
---

# GitHub Issue 기반 작업 시작 Workflow

이 워크플로우는 기존에 등록된 GitHub Issue를 선택하고, 작업 브랜치를 생성하여 개발 준비를 마치는 과정을 안내합니다.

> **Language Rule**: 논의 과정, 계획 수립, 커뮤니케이션 등 모든 과정은 **한국어**로 진행해야 합니다. (개발 용어 및 키워드는 원문 유지 가능)

## 1. 이슈 선정

사용자가 입력한 번호(Issue ID) 또는 내용을 바탕으로 작업할 GitHub Issue를 식별합니다.

```bash
# 이슈 목록 확인 (필요 시)
gh issue list
```

## 2. 작업 브랜치 생성 (즉시 실행)

이슈가 선정되면 **즉시** 작업 브랜치를 생성합니다.

-   **Base Branch**: `develop`
-   **Branch Naming**: `feature/issue-<ID>-<Short-Description>` (또는 `fix/`, `chore/` 등 성격에 맞게)

```bash
# develop 브랜치 최신화
git fetch origin develop

# 브랜치 생성 및 이동
git checkout -b feature/issue-{ID}-{description} origin/develop
```

## 3. 솔루션 논의 및 계획

브랜치 생성이 완료되면 작업을 바로 시작하지 않고, **해결 방법에 대해 사용자와 충분히 논의**합니다.

1.  **이슈 분석**: 이슈의 요구사항과 목표를 재확인합니다.
2.  **설계/계획**: 구현 방향, 기술적 고려사항, 영향 범위 등을 논의합니다.
3.  **작업 착수**: 합의된 계획에 따라 구현을 시작합니다.
