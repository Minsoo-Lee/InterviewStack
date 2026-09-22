package com.interviewstack.rag;

import com.interviewstack.domain.referencedocument.ReferenceDocument;
import com.interviewstack.domain.referencedocument.ReferenceDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 앱 기동 시 embedding이 아직 계산되지 않은 reference_documents 행을 찾아
 * Gemini 임베딩 모델로 벡터를 계산하고 채워 넣는다 (V2 시드 데이터 대응).
 *
 * GEMINI_API_KEY가 설정되지 않은 로컬 환경(예: 최초 클론 직후, CI)에서는
 * 조용히 건너뛴다 — RAG 근거자료가 비어 있을 뿐 나머지 기능(질문/답변/채점)은
 * 정상 동작해야 하므로, 여기서 예외를 던져 앱 기동을 막지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceDocumentEmbeddingRunner implements ApplicationRunner {

    private final ReferenceDocumentRepository referenceDocumentRepository;
    private final EmbeddingModel embeddingModel;

    @Value("${spring.ai.google.genai.api-key:}")
    private String geminiApiKey;

    @Override
    public void run(ApplicationArguments args) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            log.info("GEMINI_API_KEY가 설정되지 않아 reference_documents 임베딩 계산을 건너뜁니다.");
            return;
        }

        List<ReferenceDocument> pending = referenceDocumentRepository.findAllPendingEmbedding();
        if (pending.isEmpty()) {
            return;
        }

        log.info("reference_documents 임베딩 계산 시작 ({}건)", pending.size());
        int success = 0;
        for (ReferenceDocument doc : pending) {
            try {
                float[] embedding = embeddingModel.embed(doc.getTitle() + "\n" + doc.getContent());
                referenceDocumentRepository.updateEmbedding(doc.getId(), EmbeddingFormatter.toVectorLiteral(embedding));
                success++;
            } catch (Exception e) {
                log.warn("reference_documents(id={}) 임베딩 계산 실패: {}", doc.getId(), e.getMessage());
            }
        }
        log.info("reference_documents 임베딩 계산 완료 ({}/{}건 성공)", success, pending.size());
    }
}
