---
name: verify-dev-backend
description: 백엔드(Kotlin/Spring) 코드의 정합성을 검증합니다. 백엔드 로직을 수정했거나 테스트 케이스를 실행해보고 싶을 때, 혹은 "백엔드 검사해줘"라고 요청받았을 때 사용하십시오.
---

# Verify Development (Backend)

This skill runs verification for the backend modules.

## Instructions

1.  **Run Verification Script**
    Execute the backend check script:
    ```bash
    ./scripts/check-backend.sh
    ```

2.  **Troubleshooting**
    -   **Compilatin Error**: Check syntax and dependencies.
    -   **Test Failure**: Run specific failed tests with verbose output to debug.
    -   **Lint Error**: Fix formatting issues.

3.  **Completion**
    Ensure the script returns success (exit code 0) before proceeding.
