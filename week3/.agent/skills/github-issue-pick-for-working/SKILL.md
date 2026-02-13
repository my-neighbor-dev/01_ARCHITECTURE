---
name: github-issue-pick-for-working
description: 작업할 GitHub Issue를 선택하고 그에 맞는 "기능 브랜치(feature branch)"를 생성하여 개발 시작을 준비합니다. "이슈 작업 시작할게", "OO번 이슈 할래" 등의 상황에서 사용하십시오.
---

# Pick GitHub Issue & Start Work

Selects an issue and prepares the workspace (branching).

## Instructions

1.  **List Issues (Optional)**
    -   If ID is not known: `gh issue list`

2.  **Create Branch**
    -   **Base**: `develop`
    -   **Naming Convention**: `feature/issue-{ID}-{short-desc}`
    -   **Command**:
        ```bash
        git fetch origin develop
        git checkout -b feature/issue-{ID}-{desc} origin/develop
        ```

3.  **Plan Solution**
    -   **Stop**: Do not start coding immediately.
    -   **Discuss**: Analyze requirements and propose a plan in Korean.
