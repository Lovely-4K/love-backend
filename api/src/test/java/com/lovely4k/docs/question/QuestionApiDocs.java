package com.lovely4k.docs.question;

import com.lovely4k.backend.question.controller.QuestionController;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionApiDocs extends RestDocsSupport {

    private final QuestionService questionService = mock(QuestionService.class);

    @Override
    protected Object initController() {
        return new QuestionController(questionService);
    }

    @DisplayName("일일 질문을 가져오는 API")
    @Test
    void getDailyQuestion() throws Exception {
        mockMvc.perform(get("/v1/questions/daily")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-daily-question",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionId").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.questionChoices[].choice").type(JsonFieldType.STRING).description("선택지")
                        )
                ));
    }

    @DisplayName("질문 양식을 생성하는 API")
    @Test
    void createQuestionForm() throws Exception {
        CreateQuestionFormRequest request = new CreateQuestionFormRequest(
                "테스트 질문",
                List.of(new CreateQuestionFormRequest.QuestionChoiceRequest("선택지 1"))
        );

        mockMvc.perform(post("/v1/questions/question-forms")
                        .param("userId", "1")
                        .param("coupleId", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("create-question-form",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseHeaders(
                                headerWithName("Location").description("리소스 저장 경로")
                        ),
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID"),
                                parameterWithName("coupleId").description("커플 ID")
                        ),
                        requestFields(
                                fieldWithPath("questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("choices[].choice").type(JsonFieldType.STRING).description("선택지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionId").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.questionChoices[].choice").type(JsonFieldType.STRING).description("선택지")
                        )
                ));
    }

    @DisplayName("질문을 생성하는 API")
    @Test
    void createQuestion() throws Exception {
        CreateQuestionFormRequest request = new CreateQuestionFormRequest(
                "새로운 테스트 질문",
                List.of(new CreateQuestionFormRequest.QuestionChoiceRequest("선택지 1"))
        );

        mockMvc.perform(post("/v1/questions")
                        .param("coupleId", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("create-question",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseHeaders(
                                headerWithName("Location").description("리소스 저장 경로")
                        ),
                        queryParameters(
                                parameterWithName("coupleId").description("커플 ID")
                        ),
                        requestFields(
                                fieldWithPath("questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("choices[].choice").type(JsonFieldType.STRING).description("선택지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionId").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.questionChoices[].choice").type(JsonFieldType.STRING).description("선택지")
                        )
                ));
    }

    @DisplayName("질문에 답변하는 API")
    @Test
    void answerQuestion() throws Exception {
        mockMvc.perform(patch("/v1/questions/{id}/answers", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("answer-question",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("질문 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디")
                        )
                ));
    }

    @DisplayName("질문 상세 정보를 가져오는 API")
    @Test
    void getQuestionDetails() throws Exception {
        mockMvc.perform(get("/v1/questions/details/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-question-details",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("질문 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.boyAnswer").type(JsonFieldType.STRING).description("남자의 답변"),
                                fieldWithPath("body.girlAnswer").type(JsonFieldType.STRING).description("여자의 답변")
                        )
                ));
    }

}