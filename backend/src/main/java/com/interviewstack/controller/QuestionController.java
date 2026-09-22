package com.interviewstack.controller;

import com.interviewstack.service.AnswerService;
import com.interviewstack.exception.QuestionNotFoundException;

import com.interviewstack.repository.QuestionRepository;
import com.interviewstack.dto.answer.AnswerWithFeedbackResponse;
import com.interviewstack.dto.question.QuestionResponse;
import com.interviewstack.dto.answer.SubmitAnswerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final AnswerService answerService;

    @GetMapping
    public List<QuestionResponse> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subTopic,
            @RequestParam(required = false) String difficulty) {
        return questionRepository.search(category, subTopic, difficulty).stream()
                .map(QuestionResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public QuestionResponse detail(@PathVariable UUID id) {
        return questionRepository.findById(id)
                .map(QuestionResponse::from)
                .orElseThrow(() -> new QuestionNotFoundException(id));
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<AnswerWithFeedbackResponse> submitAnswer(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitAnswerRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        AnswerWithFeedbackResponse result = answerService.submitAnswer(id, principal.getUsername(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
