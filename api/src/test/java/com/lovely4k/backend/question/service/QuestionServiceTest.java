package com.lovely4k.backend.question.service;

import com.lovely4k.TestData;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    QuestionRepository questionRepository;

    @Mock
    QuestionValidator questionValidator;

    @Mock
    QuestionServiceSupporter questionServiceSupporter;

    @InjectMocks
    QuestionService questionService;

    @DisplayName("질문 양식 생성 테스트")
    @Test
    void createQuestionForm() {
        // Given
        Long coupleId = 1L;
        Long userId = 1L;
        Long questionId = 1L;
        Long questionDay = 1L;
        CreateQuestionFormServiceRequest request = new CreateQuestionFormServiceRequest("test", "test1", "test2", null, null);

        Question mockQuestion = mock(Question.class);
        QuestionForm questionForm = TestData.questionForm(userId);
        given(mockQuestion.getQuestionForm()).willReturn(questionForm);
        given(mockQuestion.getId()).willReturn(questionId);
        CreateQuestionFormResponse expectedResponse = CreateQuestionFormResponse.from(mockQuestion);

        given(questionServiceSupporter.getQuestionDay(coupleId)).willReturn(questionDay);
        given(questionRepository.save(any(Question.class))).willReturn(mockQuestion);
        willDoNothing().given(questionValidator).validateCreateQuestion(coupleId, questionDay);

        // When
        CreateQuestionFormResponse actualResponse = questionService.createQuestionForm(request, coupleId, userId);

        // Then
        Assertions.assertThat(expectedResponse).isEqualTo(actualResponse);
    }
}