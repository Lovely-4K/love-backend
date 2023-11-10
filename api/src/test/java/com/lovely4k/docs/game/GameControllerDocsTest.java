package com.lovely4k.docs.game;

import com.lovely4k.backend.game.controller.GameController;
import com.lovely4k.backend.game.service.GameQueryService;
import com.lovely4k.backend.game.service.response.FindQuestionGameResponse;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerDocsTest extends RestDocsSupport {

    private final GameQueryService gameQueryService = mock(GameQueryService.class);

    @Override
    protected Object initController() {
        return new GameController(gameQueryService);
    }

    @Test
    @DisplayName("게임을 할 경우 질문을 조회하는 API")
    void findQuestionGame() throws Exception {
        //given
        given(gameQueryService.findQuestionGame(any())).willReturn(
            new FindQuestionGameResponse(
                1L,
                "testQuestionContent",
                "firstChoice",
                "secondChoice",
                "thirdChoice",
                "fourthChoice"
            )
        );

        //when && then
        mockMvc.perform(get("/v1/games"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("find-random-question",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("응답 코드"),
                    fieldWithPath("body.questionId").type(JsonFieldType.NUMBER)
                        .description("질문 id"),
                    fieldWithPath("body.questionContent").type(JsonFieldType.STRING)
                        .description("질문 내용"),
                    fieldWithPath("body.firstChoice").type(JsonFieldType.STRING)
                        .description("1번 보기"),
                    fieldWithPath("body.secondChoice").type(JsonFieldType.STRING)
                        .description("2번 보기"),
                    fieldWithPath("body.thirdChoice").type(JsonFieldType.STRING)
                        .description("3번 보기"),
                    fieldWithPath("body.fourthChoice").type(JsonFieldType.STRING)
                        .description("4번 보기"),
                    fieldWithPath("links[0].rel").type(JsonFieldType.STRING)
                        .description("relation of url"),
                    fieldWithPath("links[0].href").type(JsonFieldType.STRING)
                        .description("url of relation")
                )
            ));

    }
}
