package com.interviewstack.question.dto;

import com.interviewstack.domain.feedback.Feedback;
import com.interviewstack.domain.referencedocument.ReferenceDocument;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FeedbackResponse(
        UUID id,
        String rubricVersion,
        Map<String, Integer> scores,
        int total,
        String summary,
        List<ReferenceDocumentResponse> references,
        boolean isWeak) {

    public static FeedbackResponse of(Feedback feedback, List<ReferenceDocument> references) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getRubricVersion(),
                feedback.getScores(),
                feedback.getTotal(),
                feedback.getSummary(),
                references.stream().map(ReferenceDocumentResponse::from).toList(),
                feedback.isWeak());
    }
}
