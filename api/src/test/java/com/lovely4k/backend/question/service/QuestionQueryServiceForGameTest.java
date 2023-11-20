package com.lovely4k.backend.question.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.repository.response.QuestionGameResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class QuestionQueryServiceForGameTest extends IntegrationTestSupport {

    @Autowired
    QuestionQueryService questionQueryService;

    @Autowired
    QuestionRepository questionRepository;

    @Test
    @DisplayName("coupleId를 통해서 기존에 답변했던 질문들 중 랜덤으로 하나를 조회할 수 있다.")
    void findQuestionGame() {
        //given
        Long coupleId = 1L;
        Sex userSex = Sex.MALE;
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm1 = QuestionForm.create(coupleId, "testQuestionContent1", questionChoices, 1L, QuestionFormType.SERVER);
        QuestionForm questionForm2 = QuestionForm.create(coupleId, "testQuestionContent2", questionChoices, 2L, QuestionFormType.SERVER);
        Question question1 = Question.create(coupleId, questionForm1, 1L);
        Question question2= Question.create(coupleId, questionForm2, 2L);

        question1.updateAnswer(1, Sex.MALE);
        question1.updateAnswer(2, Sex.FEMALE);
        question2.updateAnswer(1, Sex.MALE);
        question2.updateAnswer(2, Sex.FEMALE);

        questionRepository.save(question1);
        questionRepository.save(question2);

        //when
        QuestionGameResponse response = questionQueryService.findQuestionGame(coupleId, userSex.toString());

        //then
        Assertions.assertAll(
            () -> assertThat(response.questionContent()).isIn("testQuestionContent1", "testQuestionContent2"),
            () -> assertThat(response.opponentChoiceIndex()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("커플 남,여 모두 답변을 하지 않은 질문은 조회할 수 없다.")
    void notfindQuestionGame() {
        //given
        Long coupleId = 1L;
        Sex userSex = Sex.MALE;
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm1 = QuestionForm.create(coupleId, "testQuestionContent1", questionChoices, 1L, QuestionFormType.SERVER);
        QuestionForm questionForm2 = QuestionForm.create(coupleId, "testQuestionContent2", questionChoices, 2L, QuestionFormType.SERVER);
        Question question1 = Question.create(coupleId, questionForm1, 1L);
        Question question2 = Question.create(coupleId, questionForm2, 2L);

        question1.updateAnswer(1, Sex.MALE);
        question2.updateAnswer(1, Sex.FEMALE);

        questionRepository.save(question1);
        questionRepository.save(question2);

        //when && then
        assertThatThrownBy(() -> questionQueryService.findQuestionGame(coupleId, userSex.toString()))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(String.format("답변이 완료된 커플 질문이 없습니다. 커플 아이디: %d", coupleId));
    }
}
