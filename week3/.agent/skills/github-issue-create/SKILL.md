---
name: github-issue-create
description: 새로운 작업을 시작하기 위해 "GitHub Issue"를 생성합니다. 사용자가 이슈 생성을 명시적으로 요청하거나, 현재 대화 내용을 바탕으로 "이슈로 등록해줘"라고 할 때 사용하십시오.
---

# Create GitHub Issue

Helper skill to draft and create GitHub Issues.

## Instructions

1.  **Draft Issue**
    -   **Context-Based**: If no specific content is provided, analyze the chat history to draft an issue.
    -   **Format**: Use `markdown`.
    -   **Language**: **Korean** Only (Technical terms can be English).
    -   **Structure**:
        -   **Title**: Clear summary.
        -   **Context**: Background info.
        -   **Problem**: What is wrong or missing.
        -   **Related Files**: Path to files.
        -   **Expected Result**: Definition of Done.

2.  **Review**
    -   Show the draft to the user for approval.

3.  **Create**
    -   Use `run_command` with GitHub CLI:
        ```bash
        gh issue create --title "제목" --body "내용"
        ```

    > **Note**: If `gh` CLI is unavailable, provide the content for the user to copy-paste.
