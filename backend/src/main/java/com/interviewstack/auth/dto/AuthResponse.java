package com.interviewstack.auth.dto;

public record AuthResponse(
        String accessToken,
        UserSummary user
) {
}
