package com.interviewstack.grading;

public class GradingFailedException extends RuntimeException {

    public GradingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
