package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("queryTest")
class QuestionQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    QuestionQueryRepository questionQueryRepository;

    @Sql(scripts = "/question.sql")
    @DisplayName("memberId, questionId, Sex를 통해 my, opponent를 구분해서 적절한 profile, answer, choice_index를 넣을 수 있다.")
    @Test
    void findQuestionDetails() {
        QuestionDetailsResponse actual = questionQueryRepository.findQuestionDetails(1L, Sex.MALE, 1L);
        QuestionDetailsResponse expected = new QuestionDetailsResponse("A vs B", "A", "B", 1, 2, "MALE PROFILE", "FEMALE PROFILE");
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}