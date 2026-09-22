package com.interviewstack.repository.question;

import com.interviewstack.entity.question.Question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    /**
     * category/subTopic/difficulty 세 파라미터 모두 선택적 필터.
     * null로 넘어온 조건은 무시한다 (GET /api/questions 쿼리 파라미터 대응).
     */
    @Query("""
            SELECT q FROM Question q
            WHERE (:category IS NULL OR q.category = :category)
              AND (:subTopic IS NULL OR q.subTopic = :subTopic)
              AND (:difficulty IS NULL OR q.difficulty = :difficulty)
            ORDER BY q.createdAt
            """)
    List<Question> search(
            @Param("category") String category,
            @Param("subTopic") String subTopic,
            @Param("difficulty") String difficulty);
}
