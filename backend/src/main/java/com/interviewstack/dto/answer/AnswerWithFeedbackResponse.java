package com.interviewstack.dto.answer;

import com.interviewstack.dto.feedback.FeedbackResponse;

import com.interviewstack.entity.Answer;
import com.interviewstack.entity.Feedback;
import com.interviewstack.entity.ReferenceDocument;

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
