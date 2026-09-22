package com.interviewstack.exception;

import java.util.UUID;

public class AnswerNotFoundException extends RuntimeException {

    public AnswerNotFoundException(UUID id) {
        super("존재하지 않는 답변입니다: " + id);
    }
}
