package com.interviewstack.domain.question;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * db/migration/V1__init_schema.sql 의 questions 테이블과 대응.
 * category/difficulty는 DB CHECK 제약과 동일한 값(한글)을 그대로 사용한다.
 */
@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(name = "sub_topic", nullable = false, length = 20)
    private String subTopic;

    @Column(nullable = false, length = 4)
    private String difficulty;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "model_answer_direction", nullable = false, columnDefinition = "text")
    private String modelAnswerDirection;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;
}
