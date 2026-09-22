package com.interviewstack.exception;

public class GradingFailedException extends RuntimeException {

    public GradingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
