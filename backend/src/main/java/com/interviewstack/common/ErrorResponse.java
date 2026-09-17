package com.interviewstack.common;

public record ErrorResponse(
        String message,
        String code
) {
}
