package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.question.controller.request.AnswerQuestionRequest;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.repository.response.QuestionGameResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionControllerTest extends ControllerTestSupport {

    @DisplayName("질문지를 생성할 때 질문내용이 null이면 안된다.")
    @Test
    void createQuestionFormWithQuestionContinetIsNull() throws Exception {
        CreateQuestionFormRequest request = new CreateQuestionFormRequest(
            null,
            "선택지 1",
            "선택지 2",
            "선택지 3",
            "선택지 4");

        mockMvc.perform(
                post("/v1/questions/question-forms")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @DisplayName("질문지를 생성할 때 첫 번째 선택지가 null이면 안된다.")
    @Test
    void createQuestionFormWithFisrtChoiceIsNull() throws Exception {
        CreateQuestionFormRequest request = new CreateQuestionFormRequest(
            "컨텐츠",
            null,
            "선택지 2",
            "선택지 3",
            "선택지 4");

        mockMvc.perform(
                post("/v1/questions/question-forms")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("질문지를 생성할 때 두 번째 선택지가 null이면 안된다.")
    @Test
    void createQuestionFormWithSecondChoiceIsNull() throws Exception {
        CreateQuestionFormRequest request = new CreateQuestionFormRequest(
            "컨텐츠",
            "선택지 1",
            null,
            "선택지 3",
            "선택지 4");

        mockMvc.perform(
                post("/v1/questions/question-forms")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @DisplayName("답변을 할 때 선택한 답변 번호가 1~4를 벗어나면 안된다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void answerQuestionWithZero(int inValidNumber) throws Exception {
        AnswerQuestionRequest request = new AnswerQuestionRequest(inValidNumber);

        mockMvc.perform(patch("/v1/questions/{id}/answers", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @DisplayName("잘못된 쿼리 파라미터로 인해 Bad Request를 반환하는 경우")
    @ParameterizedTest(name = "id={0}, limit={1}")
    @CsvSource({
        "0, -1",
        "-1, 10",
    })
    void getAnsweredQuestions(Long id, int limit) throws Exception {

        mockMvc.perform(
                get("/v1/questions")
                    .queryParam("id", String.valueOf(id))
                    .queryParam("limit", String.valueOf(limit))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @Test
    @DisplayName("게임을 위해 커플이 작성한 질분들 중 하나의 질문을 랜덤으로 조회한다.")
    void getQuestionGame() throws Exception {
        //given
        given(questionQueryService.findQuestionGame(anyLong(), any())).willReturn(
            new QuestionGameResponse(
                "testQuestionContent",
                "testFirstChoice",
                "testSecondChoice",
                "testThirdChoice",
                "testFourthChoice",
                1
            )
        );

        //when && then
        mockMvc.perform(get("/v1/questions/games"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.body.questionContent").value("testQuestionContent"))
            .andExpect(jsonPath("$.body.opponentChoiceIndex").value(1))
        ;

        verify(questionQueryService).findQuestionGame(anyLong(), any());
    }
}
