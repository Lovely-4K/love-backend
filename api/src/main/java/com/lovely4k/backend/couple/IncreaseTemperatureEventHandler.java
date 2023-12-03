package com.lovely4k.backend.couple;


import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.couple.service.CoupleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class IncreaseTemperatureEventHandler {

    private final CoupleService coupleService;

    @Async
    @TransactionalEventListener(
        classes = IncreaseTemperatureEvent.class,
        phase = TransactionPhase.AFTER_COMMIT
    )
    @CacheEvict(value = CacheConstants.LOVE_TEMPERATURE, key = "#event.coupleId()")
    public void handle(IncreaseTemperatureEvent event) {
        coupleService.increaseTemperature(event.coupleId());
    }
}