# InterviewStack (인터뷰스택)

AI·백엔드 엔지니어 취업/이직 준비생을 위한, 도메인 특화 질문은행 기반 답변 첨삭과 AI 모의면접을 결합한 개인 맞춤형 기술면접 준비 서비스입니다.

질문·답변·첨삭이 차곡차곡 쌓여 실력이 되고, 기술 스택을 쌓듯 면접 역량을 쌓아간다는 의미로 "인터뷰(Interview)"와 "스택(Stack)"을 결합해 이름 지었습니다.

## 핵심 기능

- **질문은행 + 답변 첨삭**: CS 기초 · 백엔드 심화 · AI/ML · 인성 4대 카테고리, 루브릭 기반 채점 + 근거자료 제시
- **AI 모의면접 챗봇**: 이력서/JD 기반 맞춤 질문, 꼬리질문, 세션 종합 리포트

## 기술 스택

| 영역 | 기술 |
|---|---|
| 백엔드 | Java 21, Spring Boot 4.0, Spring Security, Spring AI 2.0 (Groq + 로컬 ONNX 임베딩) |
| 데이터 | PostgreSQL, pgvector |
| 프론트 | React (Vite), TypeScript, CSR SPA |
| 인프라 | Docker Compose (DB + 백엔드 + 프론트 전체 핫 리로드 개발 환경) |

## 프로젝트 구조

```
InterviewStack/
├── backend/          # Spring Boot API 서버
├── frontend/         # React (Vite) SPA
├── docs/             # API 명세, ERD 등 설계 문서
└── docker-compose.yml
```

## 로컬 개발 환경 실행

DB · 백엔드 · 프론트 전부 Docker Compose로 띄운다. 소스는 컨테이너에 바인드 마운트되어 있어서
코드를 고치면 컨테이너를 다시 빌드/재시작할 필요 없이 바로 반영된다(핫 리로드) — 백엔드는 Gradle
`--continuous` + DevTools가, 프론트는 Vite dev server가 변경을 감지해 자동으로 재시작/리프레시한다.

```bash
# 0. (최초 1회) backend/.env에 GROQ_API_KEY 설정 — 답변 첨삭(LLM 채점)에 필요
#    console.groq.com에서 발급, 카드 등록 불필요. backend/.env.example 참고
cp backend/.env.example backend/.env   # 없다면 직접 만들고 GROQ_API_KEY=... 한 줄 추가

# 1. 전체 스택(DB + 백엔드 + 프론트) 기동
docker compose up -d

# 로그 확인 (각각 따로)
docker compose logs -f backend
docker compose logs -f frontend

# 종료
docker compose down
```

프론트: http://localhost:5174, 백엔드: http://localhost:8080, DB: localhost:5432

필요하면 그룹만 골라서 띄울 수도 있다:

```bash
docker compose up -d postgres backend   # DB + 백엔드만
docker compose up -d frontend           # 프론트만
```

> **Docker 없이 로컬에서 직접 돌리고 싶다면**: `docker compose up -d postgres`로 DB만 띄운 뒤,
> `cd backend && ./gradlew bootRun`, `cd frontend && npm install && npm run dev`를 각각 실행해도 된다
> (기존 방식 그대로 동작함 — `application.yml`의 DB 접속 주소가 기본값(localhost)을 쓰기 때문).

> **왜 채점은 Groq, 임베딩은 로컬?** 2026-10-01부터 LLM 채점(`GradingService`)은 [Groq](https://console.groq.com)로
> 옮겼다 — 카드 등록 없이 가입 가능하고 무료 한도(분당 30회/일 1,000회)가 넉넉해서, 기존 Gemini 결제/쿼터(402)
> 문제의 영향을 안 받는다. RAG 근거자료 검색에 쓰는 임베딩은 Groq가 제공하지 않고, 대신 Spring AI의 로컬 ONNX
> 모델(`all-MiniLM-L6-v2`, 384차원)로 전환해서 외부 API 호출 자체를 없앴다 — 가입/카드/쿼터/결제 문제가
> 원천적으로 발생하지 않는다. 별도 API 키나 환경변수 설정이 필요 없고, 첫 실행 시 모델(~80MB)을 Hugging Face에서
> 내려받아 캐싱하므로 최초 1회만 기동이 느리고 인터넷 연결이 필요하다.
>
> **주의 1 (영어 위주 모델)**: `all-MiniLM-L6-v2`는 영어 중심으로 학습된 모델이라 한국어 질문/답변 간 유사도
> 품질은 제한적일 수 있다. 틀린 건 아니고, 더 정확한 한국어 임베딩이 필요해지면 다국어 ONNX 모델로 교체를 고려할 것.
>
> **주의 2 (기동 실패 가능성)**: Spring AI 공식 문서에 따르면 이 로컬 모델은 "fail fast" 방식이라, 모델
> 다운로드/로드에 실패하면(예: 최초 실행 시 인터넷 연결 불가) RAG 기능뿐 아니라 애플리케이션 전체가 기동되지
> 않는다 — graceful degradation 없음. 로컬 개발 환경에서는 인터넷 연결만 확인되면 문제 없다.
>
> **GROQ_API_KEY 없이 실행하면?** 서버는 정상 기동되고 회원가입/로그인/질문 목록 조회는 그대로 동작하지만,
> 답변 제출(`POST /api/questions/{id}/answers`) 시 LLM 채점이 실패해 502(`GRADING_FAILED`)가 반환된다.

> **Groq 없이(또는 쿼터 문제로) 앱 흐름만 테스트하려면**: 실제 Groq 호출 없이 미리 정해둔 가짜 채점 결과를
> 돌려준다(비용 발생 없음). RAG 근거자료는 항상 빈 목록으로 응답한다. 응답의 `summary`가 `[모의 채점 모드]`로
> 시작하므로 실제 첨삭과 혼동되지 않는다. `backend/.env`에 `AI_MOCK_MODE=true`를 추가하고
> `docker compose restart backend`(비도커 실행 시엔 `export AI_MOCK_MODE=true && ./gradlew bootRun`).

> **Boot 3.3.5 → 4.0.0**: Spring AI 2.0.x(AIAgent/RagPipeline 참고 프로젝트와 동일 라인)가 Boot 4.0/4.1만
> 지원해서 백엔드 전체를 Boot 4.0.0으로 올렸다 (2026-09-18).

## 문서

- API 명세: [`docs/openapi.yaml`](./docs/openapi.yaml)
- DB 스키마: [`backend/src/main/resources/db/migration`](./backend/src/main/resources/db/migration)

---

포트폴리오 프로젝트로 시작해, 반응이 좋으면 정식 서비스로 확장하는 것을 함께 검토하고 있습니다.
