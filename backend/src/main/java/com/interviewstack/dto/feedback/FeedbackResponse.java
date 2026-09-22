package com.interviewstack.dto.feedback;

import com.interviewstack.dto.referencedocument.ReferenceDocumentResponse;

import com.interviewstack.entity.feedback.Feedback;
import com.interviewstack.entity.referencedocument.ReferenceDocument;

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
