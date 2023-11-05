package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IncreaseTemperatureFacadeTest extends IntegrationTestSupport {

    @Autowired
    CoupleRepository coupleRepository;

    @Autowired
    IncreaseTemperatureFacade facade;

    @DisplayName("커플의 온도 관련 동시성 테스트")
    @Test
    void optimisticLock() throws InterruptedException {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .temperature(36.5f)
            .invitationCode("test-code")
            .build();

        Couple savedCouple = coupleRepository.save(couple);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        AtomicBoolean result1 = new AtomicBoolean(true);
        AtomicBoolean result2 = new AtomicBoolean(true);

        executorService.execute(() -> {
            try {
                facade.increaseTemperature(savedCouple.getId());
            } catch (InterruptedException e) {
                result1.set(Boolean.FALSE);
            }
            latch.countDown();
        });

        executorService.execute(() -> {
            try {
                facade.increaseTemperature(savedCouple.getId());
            } catch (InterruptedException e) {
                result2.set(Boolean.FALSE);
            }
            latch.countDown();
        });

        latch.await();

        // then
        Couple findCouple = coupleRepository.findById(savedCouple.getId()).orElseThrow();
        assertAll(
            () -> assertThat(findCouple.getTemperature()).isEqualTo(38.5f),
            () -> assertThat(result1.get()).isTrue(),
            () -> assertThat(result2.get()).isTrue()
        );
    }
}
