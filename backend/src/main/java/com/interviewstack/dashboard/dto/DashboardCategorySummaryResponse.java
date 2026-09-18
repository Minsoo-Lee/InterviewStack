package com.interviewstack.dashboard.dto;

public record DashboardCategorySummaryResponse(
        String category,
        double averageScore,
        long answeredCount) {
}
