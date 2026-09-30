package com.interviewstack.rag;

import com.interviewstack.domain.question.Question;
import com.interviewstack.domain.referencedocument.ReferenceDocument;
import com.interviewstack.domain.referencedocument.ReferenceDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 질문+답변을 임베딩해 reference_documents에서 pgvector 최근접 이웃을 찾는다.
 * RAG 근거자료는 채점 결과의 부가 정보이므로, 임베딩 모델 호출 실패·API 키
 * 미설정·코퍼스가 비어있는 경우 등 어떤 이유로든 실패하면 빈 목록으로
 * 안전하게 대체한다 (POST /api/questions/{id}/answers 전체를 실패시키지 않음).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceSearchService {

    private final EmbeddingModel embeddingModel;
    private final ReferenceDocumentRepository referenceDocumentRepository;

    @Value("${interviewstack.rag.top-k:3}")
    private int topK;

    @Value("${interviewstack.rag.max-distance:0.4}")
    private double maxDistance;

    @Value("${interviewstack.ai.mock-mode:false}")
    private boolean mockMode;

    public List<ReferenceDocument> search(Question question, String userAnswer) {
        if (mockMode) {
            // 모의 채점 모드에서는 임베딩 자체를 계산하지 않으므로(ReferenceDocumentEmbeddingRunner도 건너뜀)
            // 실제 호출을 시도할 필요 없이 바로 빈 목록으로 응답한다.
            return List.of();
        }
        try {
            float[] embedding = embeddingModel.embed(question.getContent() + "\n" + userAnswer);
            String literal = EmbeddingFormatter.toVectorLiteral(embedding);

            List<UUID> nearestIds = referenceDocumentRepository.findNearestIds(
                    literal, question.getCategory(), maxDistance, topK);
            if (nearestIds.isEmpty()) {
                return List.of();
            }

            Map<UUID, ReferenceDocument> byId = new LinkedHashMap<>();
            referenceDocumentRepository.findAllById(nearestIds).forEach(doc -> byId.put(doc.getId(), doc));

            return nearestIds.stream()
                    .map(byId::get)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            // GradingService와 같은 이유로 e.getMessage()만 남기지 않고 전체 스택 트레이스를 남긴다.
            log.warn("RAG 근거자료 검색 실패, 빈 목록으로 대체합니다", e);
            return List.of();
        }
    }
}
