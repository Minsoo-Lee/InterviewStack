package com.interviewstack.dto.referencedocument;

import com.interviewstack.entity.ReferenceDocument;

import java.util.UUID;

public record ReferenceDocumentResponse(
        UUID id,
        String title,
        String sourceUrl) {

    public static ReferenceDocumentResponse from(ReferenceDocument document) {
        return new ReferenceDocumentResponse(document.getId(), document.getTitle(), document.getSourceUrl());
    }
}
