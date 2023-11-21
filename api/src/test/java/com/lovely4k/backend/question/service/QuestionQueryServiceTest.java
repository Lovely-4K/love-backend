package com.lovely4k.backend.question.service;

import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.QuestionQueryRepository;
import com.lovely4k.backend.question.repository.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.repository.response.DailyQuestionResponse;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.lovely4k.backend.question.repository.response.QuestionResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuestionQueryServiceTest {

    @Mock
    QuestionQueryRepository questionQueryRepository;

    @InjectMocks
    QuestionQueryService questionQueryService;

    @DisplayName("질문의 상세 정보를 요청한 사용자와 상대방의 답변 텍스트, 프로필, 답변 번호와 함께 조회할 수 있다.")
    @Test
    void findQuestionDetails() {
        // given
        long questionId = 1L;
        long memberId = 2L;
        String picture = "test";
        QuestionDetailsResponse mockResponse = new QuestionDetailsResponse(
            "What is your favorite color?",
            "Blue",
            "Red",
            1,
            2,
            "Profile of member",
            "Profile of opponent"
        );

        given(questionQueryRepository.findQuestionDetails(questionId, memberId, picture)).willReturn(mockResponse);

        // when
        QuestionDetailsResponse result = questionQueryService.findQuestionDetails(questionId, memberId, picture);

        // then
        assertThat(result).isNotNull()
            .extracting(QuestionDetailsResponse::questionContent,
                QuestionDetailsResponse::myAnswer,
                QuestionDetailsResponse::opponentAnswer,
                QuestionDetailsResponse::myChoiceIndex,
                QuestionDetailsResponse::opponentChoiceIndex,
                QuestionDetailsResponse::myProfile,
                QuestionDetailsResponse::opponentProfile)
            .containsExactly(
                "What is your favorite color?",
                "Blue",
                "Red",
                1,
                2,
                "Profile of member",
                "Profile of opponent"
            );
    }

    @DisplayName("커플 ID에 해당하는 모든 답변된 질문들을 조회할 수 있다.")
    @Test
    void findAllAnsweredQuestionByCoupleId() {
        // given
        long id = 1L;
        long coupleId = 2L;
        int limit = 5;
        List<QuestionResponse> mockResponses = List.of(
            new QuestionResponse(1L, "What's your favorite movie?"),
            new QuestionResponse(2L, "What's your dream vacation destination?")
        );
        AnsweredQuestionResponse mockAnsweredQuestions = new AnsweredQuestionResponse(mockResponses);

        given(questionQueryRepository.findAnsweredQuestions(id, coupleId, limit)).willReturn(mockAnsweredQuestions);

        // when
        AnsweredQuestionResponse result = questionQueryService.findAllAnsweredQuestionByCoupleId(id, coupleId, limit);

        // then
        assertThat(result).isNotNull()
            .extracting(AnsweredQuestionResponse::answeredQuestions)
            .asList()
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(mockResponses);
    }

    @DisplayName("특정 커플 ID에 대한 일일 질문을 조회할 수 있다.")
    @Test
    void findDailyQuestion() {
        // given
        long coupleId = 1L;
        DailyQuestionResponse mockDailyQuestion = new DailyQuestionResponse(
            3L,
            "테스트 질문",
            "선택지1",
            "선택지2",
            "선택지3",
            "선택지4",
            QuestionFormType.CUSTOM
        );

        given(questionQueryRepository.findDailyQuestion(coupleId)).willReturn(mockDailyQuestion);

        // when
        DailyQuestionResponse result = questionQueryService.findDailyQuestion(coupleId);

        // then
        Assertions.assertThat(result).isEqualTo(mockDailyQuestion);
    }
}