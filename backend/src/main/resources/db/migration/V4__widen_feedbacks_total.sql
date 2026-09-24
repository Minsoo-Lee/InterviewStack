-- ============================================================
-- feedbacks.total 컬럼 타입 수정 (V4)
-- Feedback 엔티티의 total 필드가 Integer라서 Hibernate가 기본적으로
-- INTEGER 컬럼을 기대하는데, V1에서 SMALLINT로 만들어서 스키마 검증(ddl-auto=validate)이
-- "wrong column type encountered" 에러로 실패함.
-- 0~40 범위 체크는 그대로 유지되므로 데이터 손실 없이 안전하게 확장 가능.
-- ============================================================

ALTER TABLE feedbacks ALTER COLUMN total TYPE INTEGER;
