package com.lovely4k.backend.game.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("게임을 위해 커플이 작성한 질분들 중 하나의 질문을 랜덤으로 조회한다.")
    void findQuestionGame() throws Exception {
        //given
        given(gameSseComponent.createEmitter(anyLong())).willReturn(new SseEmitter(60000L));
        given(gameQueryService.findQuestionGame(anyLong())).willReturn(
            new FindQuestionGameResponse(
                "testQuestionContent",
                "testFirstChoice",
                "testSecondChoice",
                "testThirdChoice",
                "testFourthChoice",
                1,
                2
            )
        );
        given(gameSseComponent.readyTwoUsers(anyLong())).willReturn(true);

        //when && then
        mockMvc.perform(get("/v1/games"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(gameSseComponent).createEmitter(anyLong());
        verify(gameSseComponent).readyTwoUsers(anyLong());
        verify(gameQueryService).findQuestionGame(anyLong());
    }

}