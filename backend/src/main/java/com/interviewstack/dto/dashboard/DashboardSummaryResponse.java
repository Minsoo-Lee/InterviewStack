package com.interviewstack.dto.dashboard;

import com.interviewstack.dto.answer.AnswerWithFeedbackResponse;

import java.util.List;

public record DashboardSummaryResponse(
        List<DashboardCategorySummaryResponse> weakCategories,
        List<AnswerWithFeedbackResponse> wrongAnswerNotes) {
}
