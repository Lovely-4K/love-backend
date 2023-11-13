package com.lovely4k.backend.game.service;

import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Getter
@Slf4j
public class GameSseComponent {

    private final Map<Long, List<SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long coupleId) {
        SseEmitter sseEmitter = new SseEmitter(60000L);
        List<SseEmitter> emitters = sseEmitterMap.computeIfAbsent(coupleId, k -> new CopyOnWriteArrayList<>());
        emitters.add(sseEmitter);

        sseEmitter.onCompletion(() -> {
            log.debug("onCompletion callback");
            emitters.remove(sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            log.debug("onTimeout callback");
            emitters.remove(sseEmitter);
        });

        return sseEmitter;
    }

    public boolean readyTwoUsers(Long coupleId) {
        List<SseEmitter> emitters = sseEmitterMap.get(coupleId);
        return emitters != null && emitters.size() == 2;
    }

    public void sendQuestionToUsers(Long coupleId, FindQuestionGameResponse response) {
        List<SseEmitter> emitters = sseEmitterMap.get(coupleId);

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("question")
                    .data(response, MediaType.APPLICATION_JSON));
                emitter.complete();
            } catch (IOException e) {
                log.warn("이벤트를 사용자에게 전송하는 과정에서 예외 발생",e);
                emitter.completeWithError(e);
            }
        });
    }
}
