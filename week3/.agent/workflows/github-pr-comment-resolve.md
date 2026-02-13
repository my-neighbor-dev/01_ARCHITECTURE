---
description: PR 리뷰 코멘트 조회, 분석, 수정 작업 및 답변 등록 워크플로우
---

# GitHub PR 리뷰 코멘트 해결 워크플로우

이 워크플로우는 PR에 등록된 리뷰 코멘트를 체계적으로 분석하고, 수정 작업을 수행한 뒤 답변을 등록하여 이슈를 종결하는 과정을 가이딩합니다.

## 1. 리뷰 코멘트 조회 및 리스팅

현재 브랜치와 연결된 PR의 모든 리뷰 코멘트를 조회합니다.

```bash
# PR 번호 확인
gh pr view --json number

# 리뷰 코멘트 상세 조회 (JSON 형식 권장)
gh api /repos/:owner/:repo/pulls/<PR_NUMBER>/comments --jq '.[] | {id: .id, path: .path, line: .line, body: .body, diff_hunk: .diff_hunk}'
```

- **팁**: 해결되지 않은(unresolved) 코멘트 위주로 필터링하여 리스트를 작성합니다.

## 2. 코멘트 검토 및 작업 계획 수립

조회된 각 코멘트에 대해 다음 사항을 분석하여 사용자에게 보고하고 작업 우선순위를 결정합니다.
- **분석 내용**: 기술적 타당성, 예상 작업량, 아키텍처 영향도
- **결정 사항**: 반영(Accept), 논의 필요(Question), 반려(Decline)

## 3. 코드 수정 및 검증

결정된 작업 내용에 따라 코드를 수정하고 로컬에서 검증을 수행합니다.

1.  **코드 수정**: 제안된 리팩토링이나 버그 수정을 수행합니다.
2.  **검증**: 프로젝트 가이드에 따른 체크스크립트나 테스트를 실행합니다.
    ```bash
    ./gradlew devCheck
    ```

## 4. 변경사항 푸시 및 PR 반영

수정된 내용을 원격 브랜치에 푸시합니다.

```bash
git add .
git commit -m "refactor: address PR review comments"
git push origin HEAD
```

## 5. 리뷰 답변 등록 및 Resolve

수정 완료 보고를 각 코멘트의 Reply로 등록합니다.

- **중요**: 쉘의 특수문자 해석 오류를 방지하기 위해 답변 내용을 JSON 파일로 작성하여 등록하는 방식을 권장합니다.

```bash
# 1. 답변 내용 작성
echo '{ "body": "제안해주신 내용을 반영하여 리팩토링을 완료했습니다. 감사합니다!" }' > reply.json

# 2. 답변 등록 (comment_id는 1단계에서 확인한 id 사용)
gh api repos/:owner/:repo/pulls/<PR_NUMBER>/comments/<COMMENT_ID>/replies --input reply.json

# 3. 임시 파일 삭제
rm reply.json
```

## 6. 대화 스레드 해결 (Resolve Thread)

답변 등록 후, 해당 리뷰 스레드를 완전히 해결(Resolve) 처리하려면 GitHub GraphQL API를 사용해야 합니다.

### 1. 스레드 ID 조회 (GraphQL)

PR의 모든 리뷰 스레드와 해결 여부, 코멘트 내용을 조회하여 대상 스레드 ID(`node_id`)를 찾습니다.

```bash
gh api graphql -F owner=':owner' -F name=':repo' -F pr=<PR_NUMBER> -f query='
query($owner: String!, $name: String!, $pr: Int!) {
  repository(owner: $owner, name: $name) {
    pullRequest(number: $pr) {
      reviewThreads(last: 20) {
        nodes {
          id
          isResolved
          path
          comments(first: 1) {
            nodes {
              body
            }
          }
        }
      }
    }
  }
}'
```

### 2. 스레드 해결 처리 (Mutation)

조회한 `threadId`를 사용하여 스레드를 Resolve 상태로 변경합니다.

```bash
gh api graphql -f threadId='<THREAD_ID>' -f query='
mutation($threadId: ID!) {
  resolveReviewThread(input: {threadId: $threadId}) {
    thread {
      isResolved
    }
  }
}'
```

---
> [!TIP]
> 모든 코멘트가 해결되었다면 PR 설명(body)을 업데이트하여 최종 상태를 공유하세요.
