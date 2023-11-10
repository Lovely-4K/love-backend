package com.lovely4k.backend.game.service;

import com.lovely4k.backend.game.service.response.FindQuestionGameResponse;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GameQueryService {

    private final QuestionRepository questionRepository;

    public FindQuestionGameResponse findQuestionGame(Long coupleId) {

        return FindQuestionGameResponse.from(findOneRandomQuestion(coupleId));
    }

    private Question findOneRandomQuestion(Long coupleId) {
        return questionRepository.findOneRandomQuestion(coupleId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("생성된 커플 질문이 없습니다. 커플 아이디: %d", coupleId)));
    }
}
