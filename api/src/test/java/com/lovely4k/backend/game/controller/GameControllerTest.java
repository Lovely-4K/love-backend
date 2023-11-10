package com.lovely4k.backend.game.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.game.service.response.FindQuestionGameResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("게임을 위해 커플이 작성한 질분들 중 하나의 질문을 랜덤으로 조회한다.")
    void findQuestionGame() throws Exception {
        //given
        given(gameQueryService.findQuestionGame(anyLong())).willReturn(
            new FindQuestionGameResponse(
                1L,
                "testQuestionContent",
                "testFirstChoice",
                "testSecondChoice",
                "testThirdChoice",
                "testFourthChoice"
            )
        );

        //when && then
        mockMvc.perform(get("/v1/games"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.questionId").value(1))
            .andExpect(jsonPath("$.body.questionContent").value("testQuestionContent"));
    }

}