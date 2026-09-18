-- ============================================================
-- reference_documents 초기 코퍼스 (RAG 근거자료 제시용, v1)
-- embedding은 의도적으로 NULL로 남겨두고, 앱 기동 시
-- ReferenceDocumentEmbeddingRunner(com.interviewstack.rag)가
-- GEMINI_API_KEY로 임베딩을 계산해 채운다.
-- ============================================================

INSERT INTO reference_documents (title, content, category, source_url) VALUES
('캐시 스탬피드(Cache Stampede)',
 '캐시 스탬피드는 인기 있는 캐시 키가 만료되는 순간 다수의 요청이 동시에 캐시 미스를 겪어 DB로 몰리는 현상이다. 대표적인 방지책은 두 가지다. 첫째, 뮤텍스 락으로 첫 요청만 DB를 조회하고 나머지는 대기시키는 방식(정확하지만 대기 시간이 늘어나는 트레이드오프 존재). 둘째, TTL이 가까워질수록 조기 갱신 확률을 높이는 확률적 조기 만료(probabilistic early expiration)로 트래픽을 분산시키는 방식.',
 'CS 기초', NULL),

('트랜잭션 격리 수준(Isolation Level)',
 'SQL 표준은 READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE 네 단계의 격리 수준을 정의한다. 낮은 수준일수록 동시성(성능)은 좋아지지만 더티 리드·논리피터블 리드·팬텀 리드 같은 이상 현상에 노출된다. PostgreSQL의 기본 격리 수준은 READ COMMITTED이며, MVCC(다중 버전 동시성 제어)를 통해 락 없이도 읽기 일관성을 확보한다.',
 'CS 기초', NULL),

('인덱스와 B-Tree',
 'RDBMS의 기본 인덱스 구조는 B-Tree로, 정렬된 키를 트리 형태로 유지해 탐색·삽입·삭제를 O(log n)에 처리한다. 인덱스는 조회 성능을 높이지만 쓰기(INSERT/UPDATE/DELETE) 시 인덱스도 함께 갱신해야 하므로 쓰기 성능 저하와 저장 공간 증가라는 트레이드오프가 있다. 카디널리티가 낮은 컬럼(예: boolean)에는 B-Tree 인덱스 효과가 작다.',
 '백엔드 심화', NULL),

('HTTP 캐시 제어(Cache-Control)',
 'Cache-Control 헤더의 max-age는 캐시 신선도 유지 시간을, no-cache는 캐시된 응답을 쓰기 전 서버에 재검증을 요구하는 것을, no-store는 아예 캐시를 금지하는 것을 의미한다. ETag/Last-Modified 기반 조건부 요청(If-None-Match, If-Modified-Since)을 함께 쓰면 재검증 시 본문 재전송 없이 304 Not Modified로 대역폭을 절약할 수 있다.',
 '백엔드 심화', NULL),

('N+1 쿼리 문제',
 'ORM에서 연관 엔티티를 지연 로딩(LAZY)으로 설정했을 때, 목록 조회 후 각 항목의 연관 엔티티에 접근할 때마다 추가 쿼리가 발생해 총 N+1번의 쿼리가 실행되는 문제다. JPA에서는 fetch join, @EntityGraph, batch size 설정(hibernate.default_batch_fetch_size) 등으로 완화할 수 있다.',
 '백엔드 심화', NULL),

('RAG(Retrieval-Augmented Generation)',
 'RAG는 LLM이 답변을 생성하기 전에 벡터 검색으로 관련 문서를 먼저 조회해 프롬프트에 근거로 포함시키는 기법이다. 모델의 파라미터에 없는 최신 정보나 사내 문서를 반영할 수 있고, 환각(hallucination)을 줄이는 데 도움을 준다. 핵심 단계는 문서 임베딩·저장(인덱싱), 질의 임베딩, 유사도 검색(코사인 유사도 등), 검색 결과를 프롬프트에 삽입하는 것이다.',
 'AI/ML', NULL),

('임베딩과 코사인 유사도',
 '임베딩은 텍스트를 고차원 실수 벡터로 변환한 표현으로, 의미적으로 유사한 텍스트는 벡터 공간에서 가까운 위치에 놓인다. 두 벡터의 유사도는 보통 코사인 유사도(내적을 벡터 크기로 정규화한 값, -1~1)로 측정하며, pgvector의 <=> 연산자는 코사인 거리(1 - 코사인 유사도)를 반환한다.',
 'AI/ML', NULL),

('LLM 구조화 출력(Structured Output)',
 'LLM에게 자유 텍스트 대신 정해진 JSON 스키마로 응답하도록 요구하는 기법이다. 프롬프트에 JSON 형식을 텍스트로 지시하는 방식은 모델이 스키마를 무시하거나 파싱 불가능한 응답을 낼 위험이 있어, Spring AI의 BeanOutputConverter(.entity())처럼 라이브러리가 스키마 지시문 생성과 파싱을 함께 처리해주는 방식이나, 모델이 네이티브로 지원하는 JSON 스키마 강제 기능(response schema)을 쓰는 것이 더 안정적이다.',
 'AI/ML', NULL),

('STAR 기법',
 'STAR는 Situation(상황)-Task(과제)-Action(행동)-Result(결과) 순으로 경험을 구조화해 설명하는 면접 답변 기법이다. 구체적인 상황과 본인의 역할, 실제로 취한 행동, 정량적/정성적 결과를 순서대로 제시하면 답변의 설득력과 자기인식 수준을 모두 높일 수 있다.',
 '인성/직무', NULL),

('갈등 상황 대처와 팀 프로세스 개선',
 '팀 내 갈등(약속 불이행, 의견 충돌 등)을 다룰 때는 감정적 대응보다 사실 확인 → 당사자와의 직접 대화 → 근본 원인 파악 → 재발 방지를 위한 팀 규칙/프로세스 제안 순서로 접근한 사례가 좋은 평가를 받는다. 단순히 문제를 해결했다는 결과보다, 팀 전체의 프로세스 개선으로 이어졌는지가 자기인식과 구조화 항목에서 중요하게 평가된다.',
 '인성/직무', NULL);
