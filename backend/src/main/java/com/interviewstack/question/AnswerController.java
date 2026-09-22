package com.interviewstack.question;

import com.interviewstack.question.AnswerService;

import com.interviewstack.question.dto.AnswerWithFeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/{id}")
    public AnswerWithFeedbackResponse get(@PathVariable UUID id, @AuthenticationPrincipal UserDetails principal) {
        return answerService.getAnswerWithFeedback(id, principal.getUsername());
    }
}
