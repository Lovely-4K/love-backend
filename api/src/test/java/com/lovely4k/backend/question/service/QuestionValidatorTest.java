package com.lovely4k.backend.question.service;

import com.lovely4k.TestData;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.repository.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class QuestionValidatorTest {

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    QuestionValidator questionValidator;

    @DisplayName("질문 생성 유효성 검사")
    @Test
    void validateCreateQuestion_ValidCase() {
        // Given
        Long coupleId = 1L;
        long questionDay = 1L;
        Question mockQuestion = mock(Question.class);

        given(questionRepository.findQuestionByCoupleIdAndQuestionDay(coupleId, questionDay))
                .willReturn(List.of(mockQuestion));

        // When
        Assertions.assertThatCode(() -> questionValidator.validateCreateQuestion(coupleId, questionDay))
                .doesNotThrowAnyException();
    }

    @DisplayName("질문 생성 유효성 검사 - 질문이 2개 이상일 때")
    @Test
    void validateCreateQuestion_MoreThanTwoQuestions() {
        // Given
        Long coupleId = 1L;
        long questionDay = 1L;
        Long userId = 1L;
        QuestionForm questionForm = TestData.questionForm(userId);
        Question mockQuestion1 = TestData.question(questionForm, coupleId, "test", "test");
        Question mockQuestion2 = TestData.question(questionForm, coupleId, "test", "tes");

        given(questionRepository.findQuestionByCoupleIdAndQuestionDay(coupleId, questionDay))
                .willReturn(List.of(mockQuestion1, mockQuestion2));

        // When & Then
        Assertions.assertThatThrownBy(() -> questionValidator.validateCreateQuestion(coupleId, questionDay))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("질문 생성 유효성 검사 - 답변이 완료되지 않았을 때")
    @Test
    void validateCreateQuestion_AnswerIncomplete() {
        // Given
        Long coupleId = 1L;
        long questionDay = 1L;
        Long userId = 1L;
        QuestionForm questionForm = TestData.questionForm(userId);
        Question mockQuestion = TestData.question(questionForm, coupleId, null, null);

        given(questionRepository.findQuestionByCoupleIdAndQuestionDay(coupleId, questionDay))
                .willReturn(List.of(mockQuestion));

        // When & Then
        Assertions.assertThatThrownBy(() -> questionValidator.validateCreateQuestion(coupleId, questionDay))
                .isInstanceOf(IllegalStateException.class);
    }
}