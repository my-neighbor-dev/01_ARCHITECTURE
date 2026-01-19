# 옆집개발자 스터디 자료

이 저장소는 옆집개발자 스터디의 주차별 자료와 프로젝트를 포함합니다.

## 구조

```
lecture-service/
├── week1/          # 1주차: 레이어드 아키텍처 기초
│   ├── docs/       # 스터디 자료
│   ├── auth/       # Auth 모듈
│   ├── user/       # User 모듈
│   └── ...         # 기타 프로젝트 파일
├── week2/          # 2주차: (추가 예정)
└── README.md       # 이 파일
```

## 각 주차별 실행 방법

각 주차 폴더는 독립적인 프로젝트로 실행할 수 있습니다.

### Week 1 실행

```bash
cd week1
./gradlew bootRun
```

### Week 2 실행

```bash
cd week2
./gradlew bootRun
```

## 주차별 내용

### Week 1: 레이어드 아키텍처 기초

- Repository 인터페이스화
- Orchestrator 패턴
- API 인터페이스 계층
- Feign 패턴을 통한 외부 API 호출

자세한 내용은 [week1/docs/README.md](./week1/docs/README.md)를 참고하세요.
