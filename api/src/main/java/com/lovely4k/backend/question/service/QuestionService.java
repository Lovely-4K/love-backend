package com.lovely4k.backend.question.service;

import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.common.event.Events;
import com.lovely4k.backend.couple.IncreaseTemperatureEvent;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.repository.QuestionFormRepository;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.lovely4k.backend.common.error.ExceptionMessage.notFoundEntityMessage;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionFormRepository questionFormRepository;
    private final QuestionValidator questionValidator;
    private final QuestionServiceSupporter questionServiceSupporter;
    private static final int LOCK_TIME_OUT = 3;

    @CacheEvict(value = CacheConstants.DAILY_QUESTIONS, key = "#coupleId")
    @Transactional(timeout = LOCK_TIME_OUT)
    public CreateQuestionFormResponse createQuestionForm(CreateQuestionFormServiceRequest request, Long coupleId, Long userId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        questionValidator.validateCreateQuestionForm(coupleId, questionDay);

        Question question = Question.create(coupleId, request.toEntity(userId, questionDay), questionDay);
        Question savedQuestion = questionRepository.save(question);

        return CreateQuestionFormResponse.from(savedQuestion);
    }

    @Transactional(timeout = LOCK_TIME_OUT)
    @CacheEvict(value = CacheConstants.DAILY_QUESTIONS, key = "#coupleId")
    public CreateQuestionResponse createQuestion(Long coupleId) {
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);
        questionValidator.validateCreateQuestion(coupleId, questionDay);

        QuestionForm questionForm = questionFormRepository.findByQuestionDayAndQuestionFormType(questionDay, QuestionFormType.SERVER)
            .orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("questionForm", questionDay)));

        Question question = Question.create(coupleId, questionForm, questionDay);
        Question savedQuestion = questionRepository.save(question);

        return CreateQuestionResponse.from(savedQuestion);
    }

    @CacheEvict(value = {CacheConstants.ANSWERED_QUESTIONS, CacheConstants.QUESTION_DETAILS}, allEntries = true)
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void updateQuestionAnswer(Long id, Long coupleId, Long loginUserId, int answer) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(notFoundEntityMessage("question", id)));  // NOSONAR

        question.updateAnswer(answer, questionServiceSupporter.getBoyId(coupleId), loginUserId);
        Events.raise(new IncreaseTemperatureEvent(question.getCoupleId()));
    }

    @CacheEvict(value = {CacheConstants.QUESTION_DETAILS, CacheConstants.DAILY_QUESTIONS, CacheConstants.ANSWERED_QUESTIONS}, allEntries = true)
    @Transactional
    public void deleteQuestion() {
        questionRepository.deleteAll();
        questionFormRepository.deleteAllByQuestionFormType(QuestionFormType.CUSTOM);
    }

}