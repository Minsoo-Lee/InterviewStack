package com.interviewstack.question;

import com.interviewstack.domain.answer.Answer;
import com.interviewstack.domain.answer.AnswerRepository;
import com.interviewstack.domain.feedback.Feedback;
import com.interviewstack.domain.feedback.FeedbackRepository;
import com.interviewstack.domain.question.Question;
import com.interviewstack.domain.question.QuestionRepository;
import com.interviewstack.domain.referencedocument.ReferenceDocument;
import com.interviewstack.domain.referencedocument.ReferenceDocumentRepository;
import com.interviewstack.domain.user.User;
import com.interviewstack.domain.user.UserRepository;
import com.interviewstack.grading.GradingResult;
import com.interviewstack.grading.GradingService;
import com.interviewstack.question.dto.AnswerWithFeedbackResponse;
import com.interviewstack.rag.ReferenceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final ReferenceDocumentRepository referenceDocumentRepository;
    private final UserRepository userRepository;
    private final GradingService gradingService;
    private final ReferenceSearchService referenceSearchService;

    @Value("${interviewstack.grading.weak-threshold:28}")
    private int weakThreshold;

    public AnswerWithFeedbackResponse submitAnswer(UUID questionId, String userEmail, String content) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다: " + userEmail));

        Answer answer = answerRepository.save(new Answer(user, question, content));

        GradingResult graded = gradingService.grade(question, content);
        int total = graded.scores().values().stream().mapToInt(Integer::intValue).sum();
        boolean isWeak = total < weakThreshold;

        List<ReferenceDocument> references = referenceSearchService.search(question, content);
        List<UUID> referenceIds = references.stream().map(ReferenceDocument::getId).toList();

        Feedback feedback = feedbackRepository.save(
                new Feedback(answer, graded.scores(), total, graded.summary(), referenceIds, isWeak));

        return AnswerWithFeedbackResponse.of(answer, feedback, references);
    }

    @Transactional(readOnly = true)
    public AnswerWithFeedbackResponse getAnswerWithFeedback(UUID answerId, String userEmail) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        // 본인 답변이 아니면 존재 여부를 노출하지 않고 동일하게 404로 처리한다.
        if (!answer.getUser().getEmail().equals(userEmail)) {
            throw new AnswerNotFoundException(answerId);
        }

        Feedback feedback = feedbackRepository.findByAnswer_Id(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        List<ReferenceDocument> references = referenceDocumentRepository.findAllById(feedback.getReferenceIds());
        return AnswerWithFeedbackResponse.of(answer, feedback, references);
    }
}
