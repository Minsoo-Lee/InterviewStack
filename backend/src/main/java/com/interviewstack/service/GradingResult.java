package com.interviewstack.service;

import java.util.Map;

/**
 * ChatClient#entity(GradingResult.class)로 파싱되는 LLM 채점 결과.
 * total/is_weak/references는 LLM에게 받지 않는다 — total은 scores 합으로
 * AnswerService에서 직접 계산하고(모델이 산술을 틀릴 수 있음), is_weak도
 * 그 total 기준으로 서버가 판정하며, references는 별도로 ReferenceSearchService의
 * pgvector 검색 결과를 사용한다(첨삭 프롬프트 v1 문서의 v1→v2 개선 방향과 일치).
 */
public record GradingResult(Map<String, Integer> scores, String summary) {
}
