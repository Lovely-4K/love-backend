package com.lovely4k.backend.couple;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.common.event.Events;
import com.lovely4k.backend.couple.service.CoupleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(IncreaseTemperatureEventHandlerTest.TestConfig.class)
class IncreaseTemperatureEventHandlerTest extends IntegrationTestSupport {

    @MockBean
    private CoupleService coupleService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional
    @DisplayName("EventListener는 이벤트를 발행한 트랜잭션이 커밋된 후에 실행되어야 한다.")
    @Test
    void handler() {
        // given

        Long coupleId = 1L;
        IncreaseTemperatureEvent increaseTemperatureEvent = new IncreaseTemperatureEvent(coupleId);

        // when
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Events.raise(increaseTemperatureEvent);
        });

        // commit
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // then
        verify(coupleService, times(1)).increaseTemperature(coupleId);
    }

    @TestConfiguration
    @EnableAsync
    static class TestConfig {
        @Bean(name = "taskExecutor")
        public Executor taskExecutor() {
            return new SyncTaskExecutor(); // 동기적으로 작업을 실행하는 Executor
        }
    }


}