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
 * 로컬 ONNX 임베딩 모델로 벡터를 계산하고 채워 넣는다 (V2 시드 데이터 대응).
 *
 * 2026-10-01부터 임베딩은 외부 API(Gemini) 호출 없이 애플리케이션 안에서 직접 계산되므로
 * (spring-ai-starter-model-transformers, all-MiniLM-L6-v2) API 키 유무를 확인할 필요가 없다 -
 * 앱이 정상 기동됐다면 embeddingModel은 항상 사용 가능하다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceDocumentEmbeddingRunner implements ApplicationRunner {

    private final ReferenceDocumentRepository referenceDocumentRepository;
    private final EmbeddingModel embeddingModel;

    @Value("${interviewstack.ai.mock-mode:false}")
    private boolean mockMode;

    @Override
    public void run(ApplicationArguments args) {
        if (mockMode) {
            log.info("AI_MOCK_MODE 활성화 - reference_documents 임베딩 계산을 건너뜁니다 (RAG 근거자료는 항상 빈 목록으로 응답).");
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
