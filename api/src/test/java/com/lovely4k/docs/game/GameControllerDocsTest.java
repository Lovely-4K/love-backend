package com.lovely4k.docs.game;

import com.lovely4k.backend.game.controller.GameController;
import com.lovely4k.backend.game.service.GameQueryService;
import com.lovely4k.backend.game.service.GameSseComponent;
import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerDocsTest extends RestDocsSupport {

    private final GameQueryService gameQueryService = mock(GameQueryService.class);
    private final GameSseComponent gameSseComponent = mock(GameSseComponent.class);

    @Override
    protected Object initController() {
        return new GameController(gameQueryService, gameSseComponent);
    }

    @Test
    @DisplayName("게임을 진행하는 API")
    void findQuestionGame() throws Exception {
        //given
        given(gameSseComponent.createEmitter(any())).willReturn(new SseEmitter(60000L));
        given(gameQueryService.findQuestionGame(any())).willReturn(
            new FindQuestionGameResponse(
                "testQuestionContent",
                "firstChoice",
                "secondChoice",
                "thirdChoice",
                "fourthChoice",
                1,
                2
            )
        );
        given(gameSseComponent.readyTwoUsers(any())).willReturn(true);

        //when && then
        mockMvc.perform(get("/v1/games")
                .accept(MediaType.TEXT_EVENT_STREAM))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("find-random-question",
                preprocessResponse(prettyPrint())
            ));
    }
}
