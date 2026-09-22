package com.interviewstack.repository.feedback;

import com.interviewstack.entity.feedback.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    Optional<Feedback> findByAnswer_Id(UUID answerId);

    List<Feedback> findByAnswer_User_IdAndIsWeakTrueOrderByCreatedAtDesc(UUID userId);

    /**
     * 카테고리별 평균 총점이 낮은 순 — 대시보드 취약 카테고리 목록.
     * 사용자가 아직 아무 답변도 하지 않았으면 빈 리스트가 반환된다(200 + 빈 배열 계약 유지).
     */
    @Query("""
            SELECT q.category AS category,
                   AVG(f.total) AS averageTotal,
                   COUNT(f) AS answeredCount
            FROM Feedback f
            JOIN f.answer a
            JOIN a.question q
            WHERE a.user.id = :userId
            GROUP BY q.category
            ORDER BY AVG(f.total) ASC
            """)
    List<CategoryScoreAggregate> aggregateByCategory(@Param("userId") UUID userId);
}
