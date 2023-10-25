package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.question.controller.request.AnswerQuestionRequest;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                    .queryParam("userId", "1")
                    .queryParam("coupleId", "1")
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
                    .queryParam("userId", "1")
                    .queryParam("coupleId", "1")
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
                    .queryParam("userId", "1")
                    .queryParam("coupleId", "1")
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

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/questions/{id}/answers", 1L)
                .queryParam("sex", "MALE")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }
}