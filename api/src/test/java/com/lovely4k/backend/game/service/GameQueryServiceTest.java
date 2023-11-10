package com.lovely4k.backend.game.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.game.service.response.FindQuestionGameResponse;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class GameQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    GameQueryService gameQueryService;

    @Autowired
    QuestionRepository questionRepository;

    @Test
    @DisplayName("coupleId를 통해서 기존에 답변했던 질문들 중 랜덤으로 하나를 조회할 수 있다.")
    void findOneRandomQuestion() throws Exception {
        //given
        Long coupleId = 1L;
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm1 = QuestionForm.create(coupleId, "test1", questionChoices, 1L, QuestionFormType.SERVER);
        QuestionForm questionForm2 = QuestionForm.create(coupleId, "test2", questionChoices, 2L, QuestionFormType.SERVER);
        Question question1 = Question.create(coupleId, questionForm1, 1L);
        Question question2= Question.create(coupleId, questionForm2, 2L);
        questionRepository.save(question1);
        questionRepository.save(question2);

        //when
        FindQuestionGameResponse response = gameQueryService.findQuestionGame(coupleId);

        //then
        assertThat(response.questionContent()).isIn("test1", "test2");
    }

    @Test
    @DisplayName("커플 질문이 존재하지 않는 경우 예외가 발생한다.")
    void doesNotExistQuestion() throws Exception {
        //given
        Long coupleId = 1L;

        //when && then
        assertThatThrownBy(() -> gameQueryService.findQuestionGame(coupleId)).isInstanceOf(EntityNotFoundException.class)
            .hasMessage(String.format("생성된 커플 질문이 없습니다. 커플 아이디: %d", coupleId));

    }
}