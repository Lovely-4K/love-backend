package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.QueryTestSupport;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Disabled("단일로는 성공 하는데 왜 안되는지 모르겠네요.")
class QuestionFormRepositoryTest extends QueryTestSupport {

    @Autowired
    QuestionFormRepository questionFormRepository;

    @Transactional
    @Sql(scripts = "/questions/questionForm.sql")
    @DisplayName("커스텀해서 만든 question_form을 전부 삭제하는 명령어")
    @Test
    void deleteCustomQuestionForm() {
        questionFormRepository.deleteAllByQuestionFormType(QuestionFormType.CUSTOM);

        List<QuestionForm> result = questionFormRepository.findAll();

        Assertions.assertThat(result).hasSize(2);
    }
}