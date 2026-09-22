package com.interviewstack.domain.referencedocument;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * db/migration/V1__init_schema.sql 의 reference_documents 테이블과 대응.
 *
 * embedding(VECTOR(768)) 컬럼은 의도적으로 엔티티에 매핑하지 않는다: Hibernate에
 * pgvector 전용 UserType이 없어 JPA로 직접 다루면 별도 라이브러리가 필요해지므로,
 * 임베딩 저장/유사도 검색은 전부 ReferenceDocumentRepository의 네이티브 SQL로 처리한다
 * (com.interviewstack.rag 패키지 참고).
 */
@Entity
@Table(name = "reference_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(length = 20)
    private String category;

    @Column(name = "source_url", columnDefinition = "text")
    private String sourceUrl;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;
}
