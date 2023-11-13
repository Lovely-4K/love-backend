package com.lovely4k.backend.game.service;

import com.lovely4k.backend.question.repository.QuestionQueryRepository;
import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GameQueryService {

    private final QuestionQueryRepository questionQueryRepository;

    public FindQuestionGameResponse findQuestionGame(Long coupleId) {

        return findOneRandomQuestion(coupleId);
    }

    private FindQuestionGameResponse findOneRandomQuestion(Long coupleId) {
        return questionQueryRepository.findOneRandomQuestion(coupleId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("답변이 완료된 커플 질문이 없습니다. 커플 아이디: %d", coupleId)));
    }
}
