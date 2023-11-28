package com.lovely4k.backend.question.service;

import com.lovely4k.backend.question.repository.QuestionQueryRepository;
import com.lovely4k.backend.question.repository.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.repository.response.DailyQuestionResponse;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.lovely4k.backend.question.repository.response.QuestionGameResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QuestionQueryService {

    private final QuestionQueryRepository questionQueryRepository;

    @Cacheable(value = "questionDetails", key = "#questionId")
    public QuestionDetailsResponse findQuestionDetails(Long questionId, Long memberId, String picture) {
        return questionQueryRepository.findQuestionDetails(questionId, memberId, picture);
    }

    @Cacheable(value = "answeredQuestions", key = "#id")
    public AnsweredQuestionResponse findAllAnsweredQuestionByCoupleId(Long id, Long coupleId, Integer limit) {
        return questionQueryRepository.findAnsweredQuestions(id, coupleId, limit);
    }

    @Cacheable(value = "dailyQuestions", key = "#coupleId")
    public DailyQuestionResponse findDailyQuestion(Long coupleId) {
        return questionQueryRepository.findDailyQuestion(coupleId);
    }

    public QuestionGameResponse findQuestionGame(Long coupleId, Long loginUserId) {
        return questionQueryRepository.findQuestionGame(coupleId, loginUserId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("답변이 완료된 커플 질문이 없습니다. 커플 아이디: %d", coupleId)));
    }
}