package com.interviewstack.dto.answer;

import jakarta.validation.constraints.NotBlank;

public record SubmitAnswerRequest(
        @NotBlank(message = "답변 내용을 입력해주세요.") String content) {
}
