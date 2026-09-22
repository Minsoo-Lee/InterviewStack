package com.interviewstack.dashboard.dto;

import com.interviewstack.question.dto.AnswerWithFeedbackResponse;

import java.util.List;

public record DashboardSummaryResponse(
        List<DashboardCategorySummaryResponse> weakCategories,
        List<AnswerWithFeedbackResponse> wrongAnswerNotes) {
}
