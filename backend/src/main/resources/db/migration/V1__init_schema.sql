-- ============================================================
-- InterviewStack 초기 스키마 (V1)
-- 대상: PostgreSQL 16 + pgvector 확장
-- 참고: 노션 기술 문서 > DB 스키마(ERD) 초안, 질문은행 DB 스키마,
--       첨삭 채점 루브릭 v1 / 첨삭 프롬프트 v1
-- ============================================================

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ------------------------------------------------------------
-- users: 서비스 회원
-- ------------------------------------------------------------
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- questions: 질문은행 (노션 "질문은행" DB와 1:1 대응)
--   카테고리/세부 주제/난이도는 노션 select 옵션 값을 그대로 사용.
--   실제 서비스 데이터는 노션 질문은행 120문항을 이관하여 채운다.
-- ------------------------------------------------------------
CREATE TABLE questions (
    id                     UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category               VARCHAR(20) NOT NULL
        CHECK (category IN ('CS 기초', '백엔드 심화', 'AI/ML', '인성/직무')),
    sub_topic              VARCHAR(20) NOT NULL,
    difficulty             VARCHAR(4) NOT NULL
        CHECK (difficulty IN ('하', '중', '상')),
    content                TEXT NOT NULL,
    model_answer_direction TEXT NOT NULL, -- 채점 시 확인할 핵심 포인트 (전체 모범답안 아님)
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_questions_category ON questions (category);
CREATE INDEX idx_questions_difficulty ON questions (difficulty);

-- ------------------------------------------------------------
-- answers: 사용자가 질문에 제출한 답변
-- ------------------------------------------------------------
CREATE TABLE answers (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES questions (id) ON DELETE CASCADE,
    content     TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_answers_user_id ON answers (user_id);
CREATE INDEX idx_answers_question_id ON answers (question_id);

-- ------------------------------------------------------------
-- feedbacks: 첨삭 프롬프트 v1의 LLM 채점 결과 (answer 1건당 1건)
--   scores: 첨삭 루브릭 v1의 4개 세부 항목 점수 (기술 트랙 / 인성 트랙에 따라
--           키가 달라지므로 고정 컬럼 대신 JSONB로 저장)
--           예) {"정확성":8,"구조화":7,"실무연결성":6,"커뮤니케이션":8}
--   references: RAG(pgvector) 검색으로 근거자료 제시 시 인용한 reference_documents.id 목록
-- ------------------------------------------------------------
CREATE TABLE feedbacks (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    answer_id       UUID NOT NULL UNIQUE REFERENCES answers (id) ON DELETE CASCADE,
    rubric_version  VARCHAR(20) NOT NULL DEFAULT 'v1',
    scores          JSONB NOT NULL,
    total           SMALLINT NOT NULL CHECK (total BETWEEN 0 AND 40),
    summary         TEXT NOT NULL,
    reference_ids   UUID[] NOT NULL DEFAULT '{}',
    is_weak         BOOLEAN NOT NULL, -- 오답노트(취약 카테고리) 판단용 플래그
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- reference_documents: 첨삭 근거자료 제시를 위한 RAG 코퍼스
--   embedding 차원은 임베딩 모델 확정 후 조정 (우선 Gemini
--   text-embedding-004 기준 768차원으로 가정, MVP 단계에서 검증 필요)
-- ------------------------------------------------------------
CREATE TABLE reference_documents (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title      VARCHAR(255) NOT NULL,
    content    TEXT NOT NULL,
    category   VARCHAR(20)
        CHECK (category IN ('CS 기초', '백엔드 심화', 'AI/ML', '인성/직무')),
    source_url TEXT,
    embedding  VECTOR(768),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_reference_documents_embedding
    ON reference_documents USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- ------------------------------------------------------------
-- interview_sessions: AI 모의면접 세션
-- ------------------------------------------------------------
CREATE TABLE interview_sessions (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id      UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    resume_text  TEXT, -- 이력서 기반 맞춤 질문 생성용 (선택 입력)
    jd_text      TEXT, -- JD 기반 맞춤 질문 생성용 (선택 입력)
    status       VARCHAR(20) NOT NULL DEFAULT 'in_progress'
        CHECK (status IN ('in_progress', 'completed')),
    started_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    ended_at     TIMESTAMPTZ
);

CREATE INDEX idx_interview_sessions_user_id ON interview_sessions (user_id);

-- ------------------------------------------------------------
-- interview_messages: 세션 내 AI 질문 / 사용자 답변 / 꼬리질문 대화 기록
-- ------------------------------------------------------------
CREATE TABLE interview_messages (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id  UUID NOT NULL REFERENCES interview_sessions (id) ON DELETE CASCADE,
    role        VARCHAR(10) NOT NULL CHECK (role IN ('ai', 'user')),
    content     TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_interview_messages_session_id ON interview_messages (session_id);
