-- 임베딩 모델을 Gemini(768차원)에서 로컬 ONNX 모델(all-MiniLM-L6-v2, 384차원)로 전환하면서
-- reference_documents.embedding 컬럼 차원을 맞춘다. 지금까지 실제로 계산된 임베딩이 없어서
-- (Gemini 결제 문제로 한 번도 성공 못함) 데이터 마이그레이션 없이 차원만 바꾸면 된다.

DROP INDEX IF EXISTS idx_reference_documents_embedding;

ALTER TABLE reference_documents
    ALTER COLUMN embedding TYPE VECTOR(384);

CREATE INDEX idx_reference_documents_embedding
    ON reference_documents USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
