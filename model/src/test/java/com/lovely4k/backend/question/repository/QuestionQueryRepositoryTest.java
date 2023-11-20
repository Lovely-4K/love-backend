package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.QueryTestSupport;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.repository.response.DailyQuestionResponse;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.lovely4k.backend.question.repository.response.QuestionGameResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class QuestionQueryRepositoryTest extends QueryTestSupport {

    @Autowired
    QuestionQueryRepository questionQueryRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Disabled("왜 안되지 외래키 제약 조건 때문에 member 테이블 drop이 안된다. 근데 실제로 member 테이블을 참조하고 있는 클래스는 없" +
        "음")
    @Sql(scripts = "/questions/question.sql")
    @DisplayName("memberId, questionId, Sex를 통해 my, opponent를 구분해서 적절한 profile, answer, choice_index를 객체에 할당하여 해당 객체를 조회할 수 있다.")
    @Test
    void findQuestionDetails() {
        QuestionDetailsResponse actual = questionQueryRepository.findQuestionDetails(1L, Sex.MALE, 1L);
        QuestionDetailsResponse expected = new QuestionDetailsResponse("A vs B", "A", "B", 1, 2, "MALE PROFILE", "FEMALE PROFILE");
        assertThat(actual).isEqualTo(expected);
    }

    @Sql(scripts = "/questions/answeredQuestion.sql")
    @DisplayName("대답한 질문의 목록을 조회하는 테스트")
    @Test
    void findAnsweredQuestions() {
        AnsweredQuestionResponse firstResult = questionQueryRepository.findAnsweredQuestions(null, 1L, 10);

        AnsweredQuestionResponse afterFirstResult = questionQueryRepository.findAnsweredQuestions(11L,
            1L,
            10);

        assertAll("Answered Questions Validation",
            () -> assertThat(firstResult.answeredQuestions()).hasSize(10),
            () -> assertThat(afterFirstResult.answeredQuestions()).hasSize(10)
        );
    }

    @Sql(scripts = "/questions/dailyQuestion.sql")
    @DisplayName("오늘의 질문을 조회하는 테스트")
    @Test
    void dailyQuestions() {
        DailyQuestionResponse result = questionQueryRepository.findDailyQuestion(1L);
        DailyQuestionResponse expected = new DailyQuestionResponse(2L, "content", "first", "second", null, null, QuestionFormType.CUSTOM);

        assertThat(result).isEqualTo(expected);
    }

    @Sql(scripts = "/questions/findQuestionGame.sql")
    @Test
    @DisplayName("coupleId를 통해 랜덤으로 질문 하나를 조회할 수 있다.")
    void findQuestionGame() {
        //given
        Long coupleId = 1L;
        Sex userSex = Sex.MALE;
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm1 = QuestionForm.create(coupleId, "testQuestionContent1", questionChoices, 1L, QuestionFormType.SERVER);
        QuestionForm questionForm2 = QuestionForm.create(coupleId, "testQuestionContent2", questionChoices, 2L, QuestionFormType.SERVER);
        Question question1 = Question.create(coupleId, questionForm1, 1L);
        Question question2 = Question.create(coupleId, questionForm2, 2L);

        question1.updateAnswer(1, Sex.MALE);
        question1.updateAnswer(2, Sex.FEMALE);
        question2.updateAnswer(1, Sex.MALE);
        question2.updateAnswer(2, Sex.FEMALE);

        questionRepository.save(question1);
        questionRepository.save(question2);

        //when
        QuestionGameResponse response = questionQueryRepository.findQuestionGame(coupleId, userSex).orElseThrow();

        //then
        Assertions.assertAll(
            () -> assertThat(response.questionContent()).isIn("testQuestionContent1", "testQuestionContent2"),
            () -> assertThat(response.opponentChoiceIndex()).isEqualTo(2)
        );
    }

    @Sql(scripts = "/questions/findQuestionGame.sql")
    @Test
    @DisplayName("커플 남,여 모두 답변을 하지 않은 질문은 조회할 수 없다.")
    void notFindOneRandomQuestion() throws Exception {
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
        assertThatThrownBy(() -> questionQueryRepository.findQuestionGame(coupleId, userSex)
            .orElseThrow(() -> new EntityNotFoundException(String.format("답변이 완료된 커플 질문이 없습니다. 커플 아이디: %d", coupleId))))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(String.format("답변이 완료된 커플 질문이 없습니다. 커플 아이디: %d", coupleId));
    }
}