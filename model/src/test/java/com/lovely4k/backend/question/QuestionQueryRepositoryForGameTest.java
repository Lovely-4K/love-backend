package com.lovely4k.backend.question;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.repository.QuestionQueryRepository;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionQueryRepositoryForGameTest extends IntegrationTestSupport {

    @Autowired
    QuestionQueryRepository questionQueryRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Test
    @DisplayName("coupleId를 통해 랜덤으로 질문 하나를 조회할 수 있다.")
    void findOneRandomQuestion() throws Exception {
        //given
        Long coupleId = 1L;
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm1 = QuestionForm.create(coupleId, "testQuestionContent1", questionChoices, 1L, QuestionFormType.SERVER);
        QuestionForm questionForm2 = QuestionForm.create(coupleId, "testQuestionContent2", questionChoices, 2L, QuestionFormType.SERVER);
        Question question1 = Question.create(coupleId, questionForm1, 1L);
        Question question2 = Question.create(coupleId, questionForm2, 2L);

        question1.updateAnswer(1, Sex.MALE);
        question1.updateAnswer(1, Sex.FEMALE);
        question2.updateAnswer(1, Sex.MALE);
        question2.updateAnswer(1, Sex.FEMALE);

        questionRepository.save(question1);
        questionRepository.save(question2);

        //when
        FindQuestionGameResponse response = findOneRandomQuestionForGame(coupleId);

        //then
        assertThat(response.questionContent()).isIn("testQuestionContent1", "testQuestionContent2");
    }

    @Test
    @DisplayName("커플 남,여 모두 답변을 하지 않은 질문은 조회할 수 없다.")
    void notFindOneRandomQuestion() throws Exception {
        //given
        Long coupleId = 1L;
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
        assertThatThrownBy(() -> findOneRandomQuestionForGame(coupleId))
            .isInstanceOf(EntityNotFoundException.class);
    }

    private FindQuestionGameResponse findOneRandomQuestionForGame(Long coupleId) {
        return questionQueryRepository.findOneRandomQuestion(coupleId)
            .orElseThrow(() -> new EntityNotFoundException("남,여 모두 답변한 질문이 존재하지 않아 게임을 할 수 없습니다."));
    }
}
