package com.interviewstack.dto.question;

import com.interviewstack.entity.question.Question;

import java.util.UUID;

public record QuestionResponse(
        UUID id,
        String category,
        String subTopic,
        String difficulty,
        String content) {

    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getCategory(),
                question.getSubTopic(),
                question.getDifficulty(),
                question.getContent());
    }
}
