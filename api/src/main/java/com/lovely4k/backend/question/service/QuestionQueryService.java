package com.lovely4k.backend.question.service;

import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.repository.QuestionQueryRepository;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QuestionQueryService {

    private final QuestionQueryRepository questionQueryRepository;

    public QuestionDetailsResponse findQuestionDetails(long questionId, long memberId, Sex sex) {
        return questionQueryRepository.findQuestionDetails(questionId, sex, memberId);
    }
}