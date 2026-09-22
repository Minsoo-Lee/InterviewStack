package com.interviewstack.dto.answer;

import com.interviewstack.entity.Answer;

import java.time.Instant;
import java.util.UUID;

public record AnswerResponse(
        UUID id,
        UUID questionId,
        String content,
        Instant createdAt) {

    public static AnswerResponse from(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getContent(),
                answer.getCreatedAt());
    }
}
