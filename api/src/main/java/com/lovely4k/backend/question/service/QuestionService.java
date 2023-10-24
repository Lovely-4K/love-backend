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

        Question question = Question.builder()
                .coupleId(coupleId)
                .questionForm(request.toEntity(userId))
                .questionDay(questionDay)
                .build();
        Question savedQuestion = questionRepository.save(question);

        return CreateQuestionFormResponse.from(savedQuestion);
    }


    public DailyQuestionResponse findDailyQuestion(Long userId) {

        return new DailyQuestionResponse(1L, "test", "test1", "test2", null, null);
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