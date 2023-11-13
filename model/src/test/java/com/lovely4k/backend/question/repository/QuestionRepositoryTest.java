package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import org.junit.jupiter.api.Disabled;
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
}