package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Question> findQuestionByCoupleIdAndQuestionDay(Long coupleId, long questionDay);
}