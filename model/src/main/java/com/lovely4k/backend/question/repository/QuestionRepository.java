package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Question q WHERE q.coupleId = :coupleId AND q.questionDay = :questionDay")
    List<Question> findQuestionByCoupleIdAndQuestionDayWithLock(Long coupleId, Long questionDay);

    List<Question> findQuestionByCoupleIdAndQuestionDay(Long coupleId, Long questionDay);

    @Query(value = """
        SELECT * FROM question
        WHERE id > :id
        AND couple_id = :coupleId
        AND boy_choice_index != 0
        AND girl_choice_index != 0
        ORDER BY id DESC LIMIT :limit
        """, nativeQuery = true)
    List<Question> findQuestionsByCoupleIdWithLimit(@Param("id") Long id, @Param("coupleId") Long coupleId, @Param("limit") int limit);
}