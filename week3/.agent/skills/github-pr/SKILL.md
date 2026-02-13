---
name: github-pr
description: 현재 작업 중인 변경사항을 Git 커밋하고 원격 저장소에 푸시한 뒤, "Pull Request(PR)"를 생성합니다. "작업 끝났어 PR 올려줘", "리뷰 요청할게" 등의 상황에서 사용하십시오.
---

# Create GitHub PR

Creates a Pull Request for the current work.

## Instructions

1.  **Branch Check**
    -   If current branch is `main` or `develop`:
        -   Ask user for branch name (e.g., `feature/...`).
        -   `git checkout -b <new-branch>`
    -   Else: Continue.

2.  **Push Changes**
    -   Ensure all changes are committed.
    -   `git push origin HEAD`

3.  **Draft PR Body**
    -   **Format**: Markdown (Korean).
    -   **Content**:
        -   Link Issue (`Closes #ID`).
        -   Summary of changes (Why & What).
        -   Test/Verification status.
    -   **Show draft to user**.

4.  **Create PR**
    -   `gh pr create --base develop --title "제목" --body "본문"`
