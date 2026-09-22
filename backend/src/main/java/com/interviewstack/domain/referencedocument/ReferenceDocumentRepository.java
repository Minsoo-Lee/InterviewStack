package com.interviewstack.domain.referencedocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ReferenceDocumentRepository extends JpaRepository<ReferenceDocument, UUID> {

    /**
     * 아직 임베딩이 계산되지 않은(embedding IS NULL) 문서 목록.
     * ReferenceDocumentEmbeddingRunner가 앱 기동 시 이 목록을 채운다.
     * SELECT * 대신 엔티티에 매핑된 컬럼만 명시해 embedding(미매핑 컬럼)을 결과에서 제외한다.
     */
    @Query(value = """
            SELECT id, title, content, category, source_url, created_at
            FROM reference_documents
            WHERE embedding IS NULL
            """, nativeQuery = true)
    List<ReferenceDocument> findAllPendingEmbedding();

    @Modifying
    @Transactional
    @Query(value = "UPDATE reference_documents SET embedding = CAST(:embedding AS vector) WHERE id = :id",
            nativeQuery = true)
    void updateEmbedding(@Param("id") UUID id, @Param("embedding") String embeddingLiteral);

    /**
     * pgvector 코사인 거리(<=>) 기준 최근접 이웃 id 목록.
     * category가 null이면 전체 카테고리에서 검색한다.
     * maxDistance는 similarityThreshold(코사인 유사도)를 거리로 환산한 값(1 - similarity).
     */
    @Query(value = """
            SELECT id FROM reference_documents
            WHERE embedding IS NOT NULL
              AND (:category IS NULL OR category = :category)
              AND (embedding <=> CAST(:embedding AS vector)) <= :maxDistance
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :topK
            """, nativeQuery = true)
    List<UUID> findNearestIds(
            @Param("embedding") String embeddingLiteral,
            @Param("category") String category,
            @Param("maxDistance") double maxDistance,
            @Param("topK") int topK);
}
