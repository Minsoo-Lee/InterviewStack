package com.interviewstack.dto.dashboard;

public record DashboardCategorySummaryResponse(
        String category,
        double averageScore,
        long answeredCount) {
}
