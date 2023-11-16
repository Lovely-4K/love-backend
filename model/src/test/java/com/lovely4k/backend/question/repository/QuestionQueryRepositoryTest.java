package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.QueryTestSupport;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.repository.response.DailyQuestionResponse;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.jdbc.Sql;

class QuestionQueryRepositoryTest extends QueryTestSupport {

    @Autowired
    QuestionQueryRepository questionQueryRepository;

    @Autowired
    Environment environment;

    @Sql(scripts = "/questions/question.sql")
    @DisplayName("memberId, questionId, Sex를 통해 my, opponent를 구분해서 적절한 profile, answer, choice_index를 넣을 수 있다.")
    @Test
    void findQuestionDetails() {
        for (String env : environment.getActiveProfiles()) {
            System.out.println(env);
        }
        //System.out.println(environment.getActiveProfiles();)
        QuestionDetailsResponse actual = questionQueryRepository.findQuestionDetails(1L, Sex.MALE, 1L);
        QuestionDetailsResponse expected = new QuestionDetailsResponse("A vs B", "A", "B", 1, 2, "MALE PROFILE", "FEMALE PROFILE");
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Sql(scripts = "/questions/answeredQuestion.sql")
    @DisplayName("대답한 질문의 목록을 조회하는 테스트")
    @Test
    void findAnsweredQuestions() {
        AnsweredQuestionResponse result = questionQueryRepository.findAnsweredQuestions(0L, 1L, 10);

        Assertions.assertThat(result.answeredQuestions()).hasSize(10);
    }

    @Sql(scripts = "/questions/dailyQuestion.sql")
    @DisplayName("오늘의 질문을 조회하는 테스트")
    @Test
    void dailyQuestions() {
        DailyQuestionResponse result = questionQueryRepository.findDailyQuestion(1L);
        DailyQuestionResponse expected = new DailyQuestionResponse(2L, "content", "first", "second", null, null, QuestionFormType.CUSTOM);

        Assertions.assertThat(result).isEqualTo(expected);
    }
}