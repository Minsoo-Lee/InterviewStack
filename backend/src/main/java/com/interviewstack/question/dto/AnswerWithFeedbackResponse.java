package com.interviewstack.question.dto;

import com.interviewstack.question.dto.FeedbackResponse;

import com.interviewstack.domain.answer.Answer;
import com.interviewstack.domain.feedback.Feedback;
import com.interviewstack.domain.referencedocument.ReferenceDocument;

import java.util.List;

public record AnswerWithFeedbackResponse(
        AnswerResponse answer,
        FeedbackResponse feedback) {

    public static AnswerWithFeedbackResponse of(Answer answer, Feedback feedback, List<ReferenceDocument> references) {
        return new AnswerWithFeedbackResponse(
                AnswerResponse.from(answer),
                FeedbackResponse.of(feedback, references));
    }
}
