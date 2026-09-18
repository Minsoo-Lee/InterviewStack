package com.interviewstack.question.dto;

import com.interviewstack.domain.referencedocument.ReferenceDocument;

import java.util.UUID;

public record ReferenceDocumentResponse(
        UUID id,
        String title,
        String sourceUrl) {

    public static ReferenceDocumentResponse from(ReferenceDocument document) {
        return new ReferenceDocumentResponse(document.getId(), document.getTitle(), document.getSourceUrl());
    }
}
