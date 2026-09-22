package com.interviewstack.repository.answer;

import com.interviewstack.entity.answer.Answer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}
