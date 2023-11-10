package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled(value = "paging 관련 쿼리 확인 테스트, 필요시에 풀어서 사용하세요")
@SpringBootTest
@Sql(scripts = "/test.sql")
@ActiveProfiles("test")
class QuestionRepositoryTest {

    @Autowired
    QuestionRepository questionRepository;

    @Test
    void findQuestionsWithLimit() {
        //given
        Long questionDay = 1L;
        int limit = 1;
        long id = 0;
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "test", questionChoices, questionDay, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, questionDay);
        questionRepository.save(question);
        //when
        List<Question> result = questionRepository.findQuestionsByCoupleIdWithLimit(id, 1L, limit);

        //then
        assertThat(result).hasSize(limit);
    }

    @Test
    @DisplayName("coupleId를 통해 랜덤으로 질문 하나를 조회할 수 있다.")
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
        Question randomQuestion = questionRepository.findOneRandomQuestion(coupleId).orElseThrow();

        //then
        assertThat(randomQuestion.getQuestionForm().getQuestionContent()).isIn("test1", "test2");
    }
}