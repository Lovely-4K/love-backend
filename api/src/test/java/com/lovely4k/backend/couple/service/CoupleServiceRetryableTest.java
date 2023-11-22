package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.common.config.RetryConfig;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class CoupleServiceRetryableTest extends IntegrationTestSupport {

    @Autowired
    private CoupleService coupleService;

    @MockBean
    private CoupleRepository coupleRepository;

    @DisplayName("retryable은 3번까지 가능하다.")
    @Test
    void increaseTemperature() {
        // given
        Long coupleId = 1L;
        Couple mockCouple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .temperature(1.0f)
            .build();

        when(coupleRepository.findByIdWithOptimisticLock(coupleId))
            .thenThrow(new ObjectOptimisticLockingFailureException(Couple.class.toString(), coupleId))
            .thenThrow(new ObjectOptimisticLockingFailureException(Couple.class.toString(), coupleId))
            .thenReturn(Optional.of(mockCouple));


        // when
        Throwable thrown = catchThrowable(() -> coupleService.increaseTemperature(coupleId));

        // then
        verify(coupleRepository, times(3)).findByIdWithOptimisticLock(coupleId);
        assertNull(thrown);
    }
}