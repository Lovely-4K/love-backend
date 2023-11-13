package com.lovely4k.backend.question.service;

import com.lovely4k.backend.couple.service.IncreaseTemperatureFacade;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.QuestionFormRepository;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.lovely4k.backend.common.ExceptionMessage.notFoundEntityMessage;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionFormRepository questionFormRepository;
    private final QuestionValidator questionValidator;
    private final QuestionServiceSupporter questionServiceSupporter;
    private final IncreaseTemperatureFacade facade;
    private static final int LOCK_TIME_OUT = 3;

    @Transactional(timeout = LOCK_TIME_OUT)
    public CreateQuestionFormResponse createQuestionForm(CreateQuestionFormServiceRequest request, Long coupleId, Long userId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        questionValidator.validateCreateQuestionForm(coupleId, questionDay);

        Question question = Question.create(coupleId, request.toEntity(userId, questionDay), questionDay);
        Question savedQuestion = questionRepository.save(question);

        return CreateQuestionFormResponse.from(savedQuestion);
    }

    @Transactional(timeout = LOCK_TIME_OUT)
    public CreateQuestionResponse createQuestion(Long coupleId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        questionValidator.validateCreateQuestion(coupleId, questionDay);

        QuestionForm questionForm = questionFormRepository.findByQuestionDay(questionDay)
                .orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("questionForm", questionDay)));

        Question question = Question.create(coupleId, questionForm, questionDay);
        Question savedQuestion = questionRepository.save(question);

        return CreateQuestionResponse.from(savedQuestion);
    }

    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void updateQuestionAnswer(Long id, String sex, int answer) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(notFoundEntityMessage("question", id)));  // NOSONAR
        question.updateAnswer(answer, Sex.valueOf(sex));
        increaseTemperature(question);
    }

    private void increaseTemperature(Question question) {
        try {
            facade.increaseTemperature(question.getCoupleId());
        } catch (InterruptedException e) {  // NOSONAR
            log.warn("[System Error] Something went wrong during increasing temperature", e);
            throw new IllegalStateException("System Error Occurred",e);
        }
    }

    public DailyQuestionResponse findDailyQuestion(Long coupleId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        Question question = questionRepository
                .findQuestionByCoupleIdAndQuestionDay(coupleId, questionDay)
                .stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("question", coupleId)));

        return DailyQuestionResponse.from(question);
    }

    public QuestionDetailsResponse findQuestionDetails(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("question", id)));
        return QuestionDetailsResponse.from(question);
    }

    public AnsweredQuestionResponse findAllAnsweredQuestionByCoupleId(Long id, Long coupleId, int limit) {
        List<Question> questions= questionRepository.findQuestionsByCoupleIdWithLimit(id, coupleId, limit);
        return AnsweredQuestionResponse.from(questions);
    }

    @Transactional
    public void deleteQuestion() {
        questionRepository.deleteAll();
        questionFormRepository.deleteAllByQuestionFormType(QuestionFormType.CUSTOM);
    }

}