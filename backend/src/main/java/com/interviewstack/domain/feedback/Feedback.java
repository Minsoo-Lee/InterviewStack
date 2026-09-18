package com.interviewstack.domain.feedback;

import com.interviewstack.domain.answer.Answer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * db/migration/V1__init_schema.sql 의 feedbacks 테이블과 대응.
 * scores(JSONB), reference_ids(uuid[])는 Hibernate 6+ 네이티브 매핑을 사용하고
 * (하이버네이트 자체 유형 시스템으로 별도 라이브러리 불필요), 첨삭 프롬프트 v1의
 * LLM 응답을 그대로 담는다. total/isWeak는 서버(AnswerService)에서 계산해 채운다.
 */
@Entity
@Table(name = "feedbacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private Answer answer;

    @Column(name = "rubric_version", nullable = false, length = 20)
    private String rubricVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Integer> scores;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false, columnDefinition = "text")
    private String summary;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "reference_ids", nullable = false, columnDefinition = "uuid[]")
    private List<UUID> referenceIds = new ArrayList<>();

    @Column(name = "is_weak", nullable = false)
    private boolean isWeak;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public Feedback(Answer answer, Map<String, Integer> scores, int total, String summary,
            List<UUID> referenceIds, boolean isWeak) {
        this.answer = answer;
        this.rubricVersion = "v1";
        this.scores = scores;
        this.total = total;
        this.summary = summary;
        this.referenceIds = referenceIds;
        this.isWeak = isWeak;
    }
}
