package com.lovely4k.backend.game.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.common.sessionuser.LoginUser;
import com.lovely4k.backend.common.sessionuser.SessionUser;
import com.lovely4k.backend.game.service.GameQueryService;
import com.lovely4k.backend.game.service.response.FindQuestionGameResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(value = "/v1/games", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameQueryService gameQueryService;

    @SneakyThrows
    @GetMapping()
    public ResponseEntity<ApiResponse<FindQuestionGameResponse>> findQuestionGame(@LoginUser SessionUser user) {
        return ApiResponse.ok(gameQueryService.findQuestionGame(user.coupleId()),
            linkTo(methodOn(getClass()).findQuestionGame(user)).withSelfRel());
    }
}
