package com.interviewstack.grading;

import com.interviewstack.domain.question.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 첨삭 루브릭 v1 / 첨삭 프롬프트 v1(Notion)을 기반으로 LLM 채점을 수행한다.
 * ChatClient#entity()로 구조화 출력을 받으므로 프롬프트 자체에 JSON 형식 지시문을
 * 넣지 않는다(Spring AI의 BeanOutputConverter가 스키마 지시문을 자동으로 덧붙인다).
 *
 * 프롬프트 문서의 "Gemini는 JSON 파싱 실패율이 GPT 대비 높다"는 리스크 메모에 따라
 * 최대 2회까지 재시도한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradingService {

    private static final Set<String> TECH_CATEGORIES = Set.of("CS 기초", "백엔드 심화", "AI/ML");

    private static final String TECH_RUBRIC = """
            질문 유형이 기술 질문(CS 기초/백엔드 심화/AI-ML)이므로 다음 4항목(각 0~10점 정수)을 사용하세요:
            - 정확성: 모범답안 방향에 언급된 핵심 포인트를 얼마나 정확히 다뤘는지
            - 구조화: 정의→원인→해결 같은 논리적 순서로 구성됐는지
            - 실무 연결성: 트레이드오프·대안·실제 적용 시나리오를 담고 있는지
            - 커뮤니케이션: 명확하고 간결하게 전달했는지""";

    private static final String PERSONALITY_RUBRIC = """
            질문 유형이 인성/직무 질문이므로 다음 4항목(각 0~10점 정수)을 사용하세요:
            - 구체성: 실제 경험을 구체적 사례로 제시했는지
            - 구조화(STAR): 상황-과제-행동-결과 흐름이 있는지
            - 자기인식: 배운 점·개선점에 대한 성찰이 담겨 있는지
            - 커뮤니케이션: 명확하고 간결하게 전달했는지""";

    private static final int MAX_ATTEMPTS = 2;

    private final ChatClient.Builder chatClientBuilder;

    public GradingResult grade(Question question, String userAnswer) {
        String prompt = buildPrompt(question, userAnswer);

        RuntimeException lastError = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return chatClientBuilder.build()
                        .prompt()
                        .user(prompt)
                        .call()
                        .entity(GradingResult.class);
            } catch (RuntimeException e) {
                lastError = e;
                log.warn("LLM 채점 실패 (시도 {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
            }
        }
        throw new GradingFailedException("LLM 채점에 실패했습니다.", lastError);
    }

    private String buildPrompt(Question question, String userAnswer) {
        boolean isTech = TECH_CATEGORIES.contains(question.getCategory());
        String rubric = isTech ? TECH_RUBRIC : PERSONALITY_RUBRIC;

        return """
                당신은 백엔드/AI 엔지니어 기술 면접의 답변 첨삭관입니다. 아래 정보를 참고해 지원자의 답변을
                정해진 루브릭에 따라 객관적으로 채점하세요.

                [질문]
                %s

                [카테고리] %s / [난이도] %s

                [모범답안 방향 — 채점 시 반드시 확인할 핵심 포인트]
                %s

                [지원자 답변]
                %s

                [채점 루브릭]
                %s

                각 항목은 0~10점 정수로 채점해 scores에 담고, summary에는 2~3문장의 총평을 작성하세요.
                """.formatted(
                question.getContent(),
                question.getCategory(),
                question.getDifficulty(),
                question.getModelAnswerDirection(),
                userAnswer,
                rubric);
    }
}
