package com.lovely4k.backend.question.service;

import com.lovely4k.TestData;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.repository.QuestionFormRepository;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import com.lovely4k.backend.question.service.response.DailyQuestionResponse;
import jakarta.persistence.OptimisticLockException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    QuestionRepository questionRepository;

    @Mock
    QuestionValidator questionValidator;

    @Mock
    QuestionServiceSupporter questionServiceSupporter;

    @Mock
    QuestionFormRepository questionFormRepository;

    @InjectMocks
    QuestionService questionService;

    @DisplayName("질문 양식을 생성한다.")
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
        willDoNothing().given(questionValidator).validateCreateQuestionForm(coupleId, questionDay);

        // When
        CreateQuestionFormResponse actualResponse = questionService.createQuestionForm(request, coupleId, userId);

        // Then
        Assertions.assertThat(expectedResponse).isEqualTo(actualResponse);
        verify(questionValidator, times(1)).validateCreateQuestionForm(coupleId, questionDay);
    }

    @Test
    @DisplayName("오늘의 질문을 조회한다.")
    public void testFindDailyQuestion() {
        // Given
        Long coupleId = 1L;
        long questionDay = 1L;
        Question mockQuestion = mock(Question.class);
        QuestionForm questionForm = TestData.questionForm(1L);
        given(questionServiceSupporter.getQuestionDay(coupleId)).willReturn(questionDay);
        given(questionRepository.findQuestionByCoupleIdAndQuestionDay(coupleId, questionDay))
                .willReturn(Arrays.asList(mockQuestion));
        given(mockQuestion.getQuestionForm()).willReturn(questionForm);
        given(mockQuestion.getId()).willReturn(1L);

        // When
        DailyQuestionResponse result = questionService.findDailyQuestion(coupleId);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(DailyQuestionResponse.from(mockQuestion));
    }

    @DisplayName("질문을 생성한다.")
    @Test
    void testCreateQuestion() {
        // Given
        Long coupleId = 1L;
        long questionDay = 1L;
        Question mockQuestion = mock(Question.class);
        QuestionForm questionForm = TestData.questionForm(1L);

        given(questionServiceSupporter.getQuestionDay(coupleId)).willReturn(questionDay);
        given(questionFormRepository.findByQuestionDay(questionDay)).willReturn(Optional.of(questionForm));
        given(questionRepository.save(any(Question.class))).willReturn(mockQuestion);
        given(mockQuestion.getQuestionForm()).willReturn(questionForm);
        given(mockQuestion.getId()).willReturn(1L);

        CreateQuestionResponse expectedResponse = CreateQuestionResponse.from(mockQuestion);

        // When
        CreateQuestionResponse actualResponse = questionService.createQuestion(coupleId);

        // Then
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(questionValidator, times(1)).validateCreateQuestion(coupleId, questionDay);
    }

    @DisplayName("질문에 대한 답변을 작성한다.")
    @Test
    void testUpdateQuestionAnswer() {
        // Given
        Long questionId = 1L;
        Sex sex = Sex.MALE;
        int answer = 1;
        Question mockQuestion = mock(Question.class);

        // 성공적으로 Question을 조회할 수 있다고 가정
        given(questionRepository.findById(questionId)).willReturn(Optional.of(mockQuestion));

        // When
        questionService.updateQuestionAnswer(questionId, sex, answer);

        // Then
        verify(mockQuestion, times(1)).updateAnswer(answer, sex);
    }

    @DisplayName("updateQuestionAnswer 메서드 실패 (OptimisticLockException) 테스트")
    @Test
    void testUpdateQuestionAnswerOptimisticLockException() {
        // Given
        Long questionId = 1L;
        Sex sex = Sex.MALE;
        int answer = 1;
        Question mockQuestion = mock(Question.class);

        // 성공적으로 Question을 조회할 수 있다고 가정
        given(questionRepository.findById(questionId)).willReturn(Optional.of(mockQuestion));
        // updateAnswer 호출시 OptimisticLockException 발생
        doThrow(OptimisticLockException.class).when(mockQuestion).updateAnswer(answer, sex);

        // When & Then
        Assertions.assertThatThrownBy(() -> questionService.updateQuestionAnswer(questionId, sex, answer))
                .isInstanceOf(OptimisticLockException.class);
    }

    @DisplayName("커플 ID로 답변된 모든 질문을 조회한다.")
    @Test
    void testFindAllAnsweredQuestionByCoupleId() {
        // Given
        Long coupleId = 1L;
        int limit = 10;
        long id = 0L;

        Question mockQuestion1 = mock(Question.class);
        Question mockQuestion2 = mock(Question.class);
        QuestionForm questionForm1 = TestData.questionForm(1L);
        QuestionForm questionForm2 = TestData.questionForm(2L);

        // mockQuestion들이 리턴할 데이터 설정
        given(mockQuestion1.getQuestionForm()).willReturn(questionForm1);
        given(mockQuestion1.getId()).willReturn(1L);
        given(mockQuestion2.getQuestionForm()).willReturn(questionForm2);
        given(mockQuestion2.getId()).willReturn(2L);

        // questionRepository가 리턴할 데이터 설정
        given(questionRepository.findQuestionsByCoupleIdWithLimit(id, coupleId, limit)).willReturn(Arrays.asList(mockQuestion1, mockQuestion2));

        // When
        AnsweredQuestionResponse actualResponse = questionService.findAllAnsweredQuestionByCoupleId(id, coupleId, limit);

        // Then
        List<AnsweredQuestionResponse.QuestionResponse> expectedQuestionResponses = Arrays.asList(
                AnsweredQuestionResponse.QuestionResponse.from(mockQuestion1),
                AnsweredQuestionResponse.QuestionResponse.from(mockQuestion2)
        );
        AnsweredQuestionResponse expectedResponse = new AnsweredQuestionResponse(expectedQuestionResponses);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}