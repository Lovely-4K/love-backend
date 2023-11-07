package com.lovely4k.backend.question.service;

import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.repository.QuestionQueryRepository;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Sex sex = Sex.MALE;
        QuestionDetailsResponse mockResponse = new QuestionDetailsResponse(
            "What is your favorite color?",
            "Blue",
            "Red",
            1,
            2,
            "Profile of member",
            "Profile of opponent"
        );

        given(questionQueryRepository.findQuestionDetails(questionId, sex, memberId)).willReturn(mockResponse);

        // when
        QuestionDetailsResponse result = questionQueryService.findQuestionDetails(questionId, memberId, sex);

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
}