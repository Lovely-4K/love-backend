package com.lovely4k.backend.couple;


import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.common.event.diary.DiaryCreatedEvent;
import com.lovely4k.backend.common.event.question.QuestionUpdatedEvent;
import com.lovely4k.backend.couple.service.CoupleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TemperatureIncreaseHandler {

    private final CoupleService coupleService;

    @Async
    @TransactionalEventListener(
        classes = DiaryCreatedEvent.class,
        phase = TransactionPhase.AFTER_COMMIT
    )
    @CacheEvict(value = CacheConstants.LOVE_TEMPERATURE, key = "#event.getCoupleId()")
    public void handle(DiaryCreatedEvent event) {
        coupleService.increaseTemperature(event.getCoupleId());
    }

    @Async
    @TransactionalEventListener(
        classes = QuestionUpdatedEvent.class,
        phase = TransactionPhase.AFTER_COMMIT
    )
    @CacheEvict(value = CacheConstants.LOVE_TEMPERATURE, key = "#event.coupleId()")
    public void handle(QuestionUpdatedEvent event) {
        coupleService.increaseTemperature(event.coupleId());
    }
}