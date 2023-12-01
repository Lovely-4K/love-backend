package com.lovely4k.backend.question.service;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.common.error.exception.QuestionCreateCountExceedException;
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
    public void validateCreateQuestionForm(Long coupleId, long questionDay) {
        List<Question> questions = findQuestionWithLock(coupleId, questionDay);
        validateDailyQuestionLimitAndAnswerCompletion(questions);
    }

    private void validateDailyQuestionLimitAndAnswerCompletion(List<Question> questions) {
        if (questions.size() > 1) {
            throw new QuestionCreateCountExceedException();
        }

        if (questions.isEmpty()) {
            throw new IllegalStateException("서버에서 제공하는 질문을 먼저 생성해 주세요.");
        }

        questions.get(0).validateAnswer();
    }

    private List<Question> findQuestionWithLock(Long coupleId, Long questionDay) {
        return questionRepository.findQuestionByCoupleIdAndQuestionDayWithLock(coupleId, questionDay);
    }

    public void validateCreateQuestion(Long coupleId, Long questionDay) {
        List<Question> questions = findQuestionWithLock(coupleId, questionDay);
        if (!questions.isEmpty()) {
            throw new IllegalStateException("이미 우이삭에서 제공해주는 오늘의 질문을 받았습니다.");
        }
    }

}