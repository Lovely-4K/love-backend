package com.lovely4k.backend.question.service;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import com.lovely4k.backend.question.service.response.DailyQuestionResponse;
import com.lovely4k.backend.question.service.response.QuestionDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.lovely4k.backend.common.ExceptionMessage.notFoundEntityMessage;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionValidator questionValidator;
    private final QuestionServiceSupporter questionServiceSupporter;

    @Transactional(timeout = 3)
    public CreateQuestionFormResponse createQuestionForm(CreateQuestionFormServiceRequest request, Long coupleId, Long userId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        questionValidator.validateCreateQuestion(coupleId, questionDay);

        Question question = Question.create(coupleId, request.toEntity(userId, questionDay), questionDay);
        Question savedQuestion = questionRepository.save(question);

        return CreateQuestionFormResponse.from(savedQuestion);
    }


    @Transactional(readOnly = true)
    public DailyQuestionResponse findDailyQuestion(Long coupleId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        Question question = questionRepository
                .findQuestionByCoupleIdAndQuestionDay(coupleId, questionDay)
                .stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new NoSuchElementException(notFoundEntityMessage("question", coupleId)));
        return DailyQuestionResponse.from(question);
    }

    public CreateQuestionResponse createQuestion(Long coupleId) {

        return new CreateQuestionResponse(1L, "test", "test1", "test2", null, null);
    }

    public void updateQuestionAnswer() {
    }

    public QuestionDetailsResponse findQuestionDetails(Long id) {

        return new QuestionDetailsResponse("test", "boy", "girl");
    }
}