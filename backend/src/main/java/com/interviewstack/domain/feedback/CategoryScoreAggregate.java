package com.interviewstack.domain.feedback;

/**
 * FeedbackRepository#aggregateByCategory 결과 프로젝션.
 * (카테고리별 평균 총점 · 답변 수 — 대시보드 취약 카테고리 산출용)
 */
public interface CategoryScoreAggregate {
    String getCategory();

    Double getAverageTotal();

    Long getAnsweredCount();
}
