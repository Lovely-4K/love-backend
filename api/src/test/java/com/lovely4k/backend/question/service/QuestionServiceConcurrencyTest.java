package com.lovely4k.backend.question.service;

import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

//H2 환경에서는 동작하지 않음
@Disabled
@SpringBootTest
@Sql(scripts = "/test.sql")
class QuestionServiceConcurrencyTest {

    @Autowired
    QuestionService questionService;

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
}