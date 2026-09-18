# InterviewStack (인터뷰스택)

AI·백엔드 엔지니어 취업/이직 준비생을 위한, 도메인 특화 질문은행 기반 답변 첨삭과 AI 모의면접을 결합한 개인 맞춤형 기술면접 준비 서비스입니다.

질문·답변·첨삭이 차곡차곡 쌓여 실력이 되고, 기술 스택을 쌓듯 면접 역량을 쌓아간다는 의미로 "인터뷰(Interview)"와 "스택(Stack)"을 결합해 이름 지었습니다.

## 핵심 기능

- **질문은행 + 답변 첨삭**: CS 기초 · 백엔드 심화 · AI/ML · 인성 4대 카테고리, 루브릭 기반 채점 + 근거자료 제시
- **AI 모의면접 챗봇**: 이력서/JD 기반 맞춤 질문, 꼬리질문, 세션 종합 리포트

## 기술 스택

| 영역 | 기술 |
|---|---|
| 백엔드 | Java 21, Spring Boot 4.0, Spring Security, Spring AI 2.0 (Gemini API) |
| 데이터 | PostgreSQL, pgvector |
| 프론트 | React (Vite), TypeScript, CSR SPA |
| 인프라 | Docker Compose (로컬 개발) |

## 프로젝트 구조

```
InterviewStack/
├── backend/          # Spring Boot API 서버
├── frontend/         # React (Vite) SPA
├── docs/             # API 명세, ERD 등 설계 문서
└── docker-compose.yml
```

## 로컬 개발 환경 실행

```bash
# 1. 인프라(PostgreSQL + pgvector) 기동
docker compose up -d

# 2. 백엔드 실행 (Java 21)
cd backend
export GEMINI_API_KEY=발급받은-Gemini-API-키   # 답변 첨삭(LLM 채점)·RAG 근거자료 검색에 필요
./gradlew bootRun

# 3. 프론트 실행
cd frontend
npm install
npm run dev
```

> **GEMINI_API_KEY 없이 실행하면?** 서버는 정상 기동되고 회원가입/로그인/질문 목록 조회는 그대로 동작하지만,
> 답변 제출(`POST /api/questions/{id}/answers`) 시 LLM 채점이 실패해 502(`GRADING_FAILED`)가 반환된다.
> RAG 근거자료 검색은 실패해도 빈 배열로 안전하게 대체되어 채점 자체를 막지 않는다.

> **Boot 3.3.5 → 4.0.0**: Spring AI 2.0.x(AIAgent/RagPipeline 참고 프로젝트와 동일 라인)가 Boot 4.0/4.1만
> 지원해서 백엔드 전체를 Boot 4.0.0으로 올렸다 (2026-09-18).

## 문서

- API 명세: [`docs/openapi.yaml`](./docs/openapi.yaml)
- DB 스키마: [`backend/src/main/resources/db/migration`](./backend/src/main/resources/db/migration)

---

포트폴리오 프로젝트로 시작해, 반응이 좋으면 정식 서비스로 확장하는 것을 함께 검토하고 있습니다.
