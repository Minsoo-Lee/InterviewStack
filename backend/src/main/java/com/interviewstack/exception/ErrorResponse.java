package com.interviewstack.exception;

public record ErrorResponse(
        String message,
        String code
) {
}
