package com.interviewstack.dashboard;

import com.interviewstack.dashboard.dto.DashboardCategorySummaryResponse;
import com.interviewstack.dashboard.dto.DashboardSummaryResponse;
import com.interviewstack.domain.feedback.CategoryScoreAggregate;
import com.interviewstack.domain.feedback.Feedback;
import com.interviewstack.domain.feedback.FeedbackRepository;
import com.interviewstack.domain.referencedocument.ReferenceDocument;
import com.interviewstack.domain.referencedocument.ReferenceDocumentRepository;
import com.interviewstack.domain.user.User;
import com.interviewstack.domain.user.UserRepository;
import com.interviewstack.question.dto.AnswerWithFeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * GET /api/dashboard: 카테고리별 평균 점수(취약 카테고리)와 오답노트(is_weak=true 답변) 조회.
 * 아직 답변이 없는 신규 사용자는 두 목록 모두 빈 배열로, 200 OK를 반환한다
 * (프론트 DashboardPage.tsx가 에러와 "데이터 없음"을 구분하지 않으므로 반드시 200이어야 함).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final ReferenceDocumentRepository referenceDocumentRepository;

    public DashboardSummaryResponse getSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다: " + userEmail));

        UUID userId = user.getId();

        List<DashboardCategorySummaryResponse> weakCategories = feedbackRepository.aggregateByCategory(userId).stream()
                .map(this::toCategorySummary)
                .toList();

        List<AnswerWithFeedbackResponse> wrongAnswerNotes = feedbackRepository
                .findByAnswer_User_IdAndIsWeakTrueOrderByCreatedAtDesc(userId).stream()
                .map(this::toAnswerWithFeedback)
                .toList();

        return new DashboardSummaryResponse(weakCategories, wrongAnswerNotes);
    }

    private DashboardCategorySummaryResponse toCategorySummary(CategoryScoreAggregate aggregate) {
        double averageScore = Math.round(aggregate.getAverageTotal() * 10) / 10.0;
        return new DashboardCategorySummaryResponse(aggregate.getCategory(), averageScore, aggregate.getAnsweredCount());
    }

    private AnswerWithFeedbackResponse toAnswerWithFeedback(Feedback feedback) {
        List<ReferenceDocument> references = referenceDocumentRepository.findAllById(feedback.getReferenceIds());
        return AnswerWithFeedbackResponse.of(feedback.getAnswer(), feedback, references);
    }
}
