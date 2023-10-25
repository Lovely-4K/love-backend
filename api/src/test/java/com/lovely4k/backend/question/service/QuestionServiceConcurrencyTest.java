package com.lovely4k.backend.question.service;

import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.repository.QuestionRepository;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

//H2 환경에서는 동작하지 않음
//@Disabled
@SpringBootTest
@Sql(scripts = "/test.sql")
class QuestionServiceConcurrencyTest {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @DisplayName("커플이 동시에 질문지를 생성할 때 한 쪽의 질문지만 생성 된다.")
    @Test
    void testConcurrency() throws InterruptedException {
        final int numberOfThreads = 2;
        final ExecutorService service = Executors.newFixedThreadPool(10);
        final CountDownLatch latch = new CountDownLatch(numberOfThreads);
        final AtomicInteger successCounter = new AtomicInteger(0);
        final AtomicInteger failureCounter = new AtomicInteger(0);
        final CreateQuestionFormRequest request = new CreateQuestionFormRequest(
                "테스트 질문",
                "선택지 1",
                "선택지 2",
                "선택지 3",
                "선택지 4");
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    questionService.createQuestionForm(request.toServiceDto(), 1L, 1L);
                     successCounter.incrementAndGet();
                } catch (IllegalStateException e) {
                    failureCounter.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertThat(successCounter.get()).isEqualTo(1);
        assertThat(failureCounter.get()).isEqualTo(1);
    }

    @Test
    void testUpdateQuestionAnswer_Concurrency() throws InterruptedException {
        final int numberOfThreads = 2;
        final ExecutorService service = Executors.newFixedThreadPool(20);
        final CountDownLatch latch = new CountDownLatch(numberOfThreads);
        final AtomicInteger successCounter = new AtomicInteger(0);
        final AtomicInteger requestCounter = new AtomicInteger(0);
        final long questionId = 1L;

        Question initialQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));
        long initialVersion = initialQuestion.getVersion();

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    int currentRequest = requestCounter.incrementAndGet();
                    int answer = (currentRequest == 1) ? 3 : 4;
                    questionService.updateQuestionAnswer(questionId, Sex.MALE, answer);
                    successCounter.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Question updatedQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));
        long updatedVersion = updatedQuestion.getVersion();
        System.out.println("초기 버전" +initialVersion);
        System.out.println("이후 버전" + updatedVersion);
        assertThat(successCounter.get()).isGreaterThan(0);
        assertThat(updatedVersion).isGreaterThan(initialVersion);
    }

}