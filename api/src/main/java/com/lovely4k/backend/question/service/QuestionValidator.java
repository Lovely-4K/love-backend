package com.lovely4k.backend.question.service;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class QuestionValidator {

    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public void validateCreateQuestion(Long coupleId, long questionDay) {
        List<Question> questions = questionRepository.findQuestionByCoupleIdAndQuestionDayWithLock(coupleId, questionDay);
        validateDailyQuestionLimitAndAnswerCompletion(questions);
    }

    private void validateDailyQuestionLimitAndAnswerCompletion(List<Question> questions) {
        if (questions.size() > 1) {
            throw new IllegalStateException("이미 오늘의 질문을 2번 생성 했습니다.");
        }
        Question question = questions.get(0);
        question.validateAnswer();
    }

}