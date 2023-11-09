package com.lovely4k.backend.couple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncreaseTemperatureFacade {

    private final CoupleService coupleService;

    public void increaseTemperature(Long coupleId) throws InterruptedException {
        while (true) {
            try {
                coupleService.increaseTemperature(coupleId);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
