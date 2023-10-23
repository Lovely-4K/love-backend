package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findQuestionByCoupleIdAndQuestionDay(Long coupleId, long questionDay);
}