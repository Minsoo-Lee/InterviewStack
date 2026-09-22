package com.interviewstack.dto;

public record ErrorResponse(
        String message,
        String code
) {
}
