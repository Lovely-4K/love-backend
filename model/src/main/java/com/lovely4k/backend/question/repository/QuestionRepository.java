package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Question q WHERE q.coupleId = :coupleId AND q.questionDay = :questionDay")
    List<Question> findQuestionByCoupleIdAndQuestionDayWithLock(Long coupleId, Long questionDay);

    List<Question> findQuestionByCoupleIdAndQuestionDay(Long coupleId, Long questionDay);
}