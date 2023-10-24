package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionControllerTest extends ControllerTestSupport {

    @DisplayName("질문지가 null이면 안된다.")
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
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("첫 번째 선택지가 null이면 안된다.")
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

    @DisplayName("두 번째 선택지가 null이면 안된다.")
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
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("첫 번째 선택지가 null이면 안된다.")
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

    @DisplayName("두 번째 선택지가 null이면 안된다.")
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
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }
}