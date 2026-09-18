package com.interviewstack.question;

import java.util.UUID;

public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(UUID id) {
        super("존재하지 않는 질문입니다: " + id);
    }
}
