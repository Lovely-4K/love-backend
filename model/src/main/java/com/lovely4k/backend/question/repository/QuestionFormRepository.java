package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionFormRepository extends JpaRepository<QuestionForm, Long> {
    Optional<QuestionForm> findByQuestionDay(Long questionDay);

    void deleteAllByQuestionFormType(QuestionFormType questionFormType);
}