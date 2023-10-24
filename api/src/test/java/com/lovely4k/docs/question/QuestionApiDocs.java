package com.lovely4k.docs.question;

import com.lovely4k.backend.question.controller.QuestionController;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import com.lovely4k.backend.question.service.response.DailyQuestionResponse;
import com.lovely4k.backend.question.service.response.QuestionDetailsResponse;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        DailyQuestionResponse mockResponse = new DailyQuestionResponse(
                1L, "테스트 질문", "선택지 1", "선택지 2", null, null
        );

        given(questionService.findDailyQuestion(1L)).willReturn(mockResponse);

        mockMvc.perform(get("/v1/questions/daily")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-daily-question",
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionId").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.firstChoice").type(JsonFieldType.STRING).description("첫 번째 선택지"),
                                fieldWithPath("body.secondChoice").type(JsonFieldType.STRING).description("두 번째 선택지"),
                                fieldWithPath("body.thirdChoice").type(JsonFieldType.STRING).optional().description("세 번째 선택지"),
                                fieldWithPath("body.fourthChoice").type(JsonFieldType.STRING).optional().description("네 번째 선택지")
                        )
                ));
    }


    @DisplayName("사용자의 질문 양식을 생성하는 API")
    @Test
    void createQuestionForm() throws Exception {
        CreateQuestionFormRequest request = new CreateQuestionFormRequest(
                "테스트 질문",
                "선택지 1",
                "선택지 2",
                "선택지 3",
                "선택지 4");

        CreateQuestionFormResponse mockResponse = new CreateQuestionFormResponse(
                1L, "테스트 질문", "선택지 1", "선택지 2", "선택지 3", "선택지 4"
        );

        given(questionService.createQuestionForm(any(CreateQuestionFormServiceRequest.class), anyLong(), anyLong()))
                .willReturn(mockResponse);

        mockMvc.perform(post("/v1/questions/question-forms")
                        .param("userId", "1")
                        .param("coupleId", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("create-question-form-and-question-by-user",
                        responseHeaders(
                                headerWithName("Location").description("리소스 저장 경로")
                        ),
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID"),
                                parameterWithName("coupleId").description("커플 ID")
                        ),
                        requestFields(
                                fieldWithPath("questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("firstChoice").type(JsonFieldType.STRING).description("첫 번째 선택지"),
                                fieldWithPath("secondChoice").type(JsonFieldType.STRING).description("두 번째 선택지"),
                                fieldWithPath("thirdChoice").type(JsonFieldType.STRING).description("세 번째 선택지"),
                                fieldWithPath("fourthChoice").type(JsonFieldType.STRING).description("네 번째 선택지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionId").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.firstChoice").type(JsonFieldType.STRING).description("첫 번째 선택지"),
                                fieldWithPath("body.secondChoice").type(JsonFieldType.STRING).description("두 번째 선택지"),
                                fieldWithPath("body.thirdChoice").type(JsonFieldType.STRING).description("세 번째 선택지"),
                                fieldWithPath("body.fourthChoice").type(JsonFieldType.STRING).description("네 번째 선택지")
                        )
                ));
    }


    @DisplayName("질문을 생성하는 API")
    @Test
    void createQuestion() throws Exception {
        CreateQuestionResponse mockResponse = new CreateQuestionResponse(
                1L, "테스트 질문", "선택지 1", "선택지 2", null, null
        );

        given(questionService.createQuestion(1L)).willReturn(mockResponse);

        mockMvc.perform(post("/v1/questions")
                        .queryParam("coupleId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("create-question",
                        responseHeaders(
                                headerWithName("Location").description("리소스 저장 경로")
                        ),
                        queryParameters(
                                parameterWithName("coupleId").description("커플 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body.questionId").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("body.questionContent").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("body.firstChoice").type(JsonFieldType.STRING).description("첫 번째 선택지"),
                                fieldWithPath("body.secondChoice").type(JsonFieldType.STRING).description("두 번째 선택지"),
                                fieldWithPath("body.thirdChoice").type(JsonFieldType.STRING).optional().description("세 번째 선택지"),
                                fieldWithPath("body.fourthChoice").type(JsonFieldType.STRING).optional().description("네 번째 선택지")
                        )
                ));
    }


    @DisplayName("질문에 답변하는 API")
    @Test
    void answerQuestion() throws Exception {
        // questionService.updateQuestionAnswer()가 호출되면 아무런 동작도 하지 않도록 설정
        willDoNothing().given(questionService).updateQuestionAnswer();

        mockMvc.perform(patch("/v1/questions/{id}/answers", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("answer-question",
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
        // questionService.findQuestionDetails()가 호출되면 미리 정의된 QuestionDetailsResponse 객체를 반환하도록 설정
        QuestionDetailsResponse mockResponse = new QuestionDetailsResponse("테스트 질문", "남자의 답변", "여자의 답변");
        given(questionService.findQuestionDetails(1L)).willReturn(mockResponse);

        mockMvc.perform(get("/v1/questions/details/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-question-details",
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