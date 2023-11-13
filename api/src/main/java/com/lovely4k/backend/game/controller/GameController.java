package com.lovely4k.backend.game.controller;

import com.lovely4k.backend.common.sessionuser.LoginUser;
import com.lovely4k.backend.common.sessionuser.SessionUser;
import com.lovely4k.backend.game.service.GameQueryService;
import com.lovely4k.backend.game.service.GameSseComponent;
import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping(value = "/v1/games")
@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameQueryService gameQueryService;
    private final GameSseComponent gameSseComponent;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> findQuestionGame(@LoginUser SessionUser sessionUser) {
        SseEmitter sseEmitter = gameSseComponent.createEmitter(sessionUser.coupleId());

        FindQuestionGameResponse response = gameQueryService.findQuestionGame(sessionUser.coupleId());

        if (gameSseComponent.readyTwoUsers(sessionUser.coupleId())) {
            gameSseComponent.sendQuestionToUsers(sessionUser.coupleId(), response);
        }

        return ResponseEntity.ok(sseEmitter);
    }
}
