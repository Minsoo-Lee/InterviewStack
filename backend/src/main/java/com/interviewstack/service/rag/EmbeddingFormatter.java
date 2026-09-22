package com.interviewstack.service.rag;

/**
 * Spring AI EmbeddingModel#embed(String)이 반환하는 float[]를
 * pgvector가 이해하는 텍스트 리터럴("[0.1,0.2,...]")로 변환한다.
 */
final class EmbeddingFormatter {

    private EmbeddingFormatter() {
    }

    static String toVectorLiteral(float[] embedding) {
        StringBuilder sb = new StringBuilder(embedding.length * 8 + 2);
        sb.append('[');
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
