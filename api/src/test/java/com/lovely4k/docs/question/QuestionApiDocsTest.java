package com.lovely4k.docs.question;

import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;
import com.lovely4k.backend.question.controller.QuestionController;
import com.lovely4k.backend.question.controller.request.AnswerQuestionRequest;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.lovely4k.backend.question.service.QuestionQueryService;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import com.lovely4k.backend.question.service.response.*;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionApiDocsTest extends RestDocsSupport {

    private final QuestionService questionService = mock(QuestionService.class);
    private final QuestionQueryService questionQueryService = mock(QuestionQueryService.class);

    @Override
    protected Object initController() {
        return new QuestionController(questionService, questionQueryService);
    }

    @DisplayName("일일 질문을 가져오는 API")
    @Test
    void getDailyQuestion() throws Exception {
        DailyQuestionResponse mockResponse = new DailyQuestionResponse(
            1L, "테스트 질문", "선택지 1", "선택지 2", null, null, QuestionFormType.SERVER
        );

        given(questionService.findDailyQuestion(1L)).willReturn(mockResponse);

        mockMvc.perform(get("/v1/questions/daily")
                .param("coupleId", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-daily-question",
                queryParameters(
                    parameterWithName("coupleId").description("커플 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.questionId").type(NUMBER).description("질문 ID"),
                    fieldWithPath("body.questionContent").type(STRING).description("질문 내용"),
                    fieldWithPath("body.firstChoice").type(STRING).description("첫 번째 선택지"),
                    fieldWithPath("body.secondChoice").type(STRING).description("두 번째 선택지"),
                    fieldWithPath("body.thirdChoice").type(STRING).optional().description("세 번째 선택지"),
                    fieldWithPath("body.fourthChoice").type(STRING).optional().description("네 번째 선택지"),
                    fieldWithPath("body.questionFormType").type(STRING).description("질문의 타입 (서버인지 커스텀인지)"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
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
            1L, "테스트 질문", "선택지 1", "선택지 2", "선택지 3", "선택지 4", QuestionFormType.SERVER
        );

        given(questionService.createQuestionForm(any(CreateQuestionFormServiceRequest.class), anyLong(), anyLong()))
            .willReturn(mockResponse);

        mockMvc.perform(post("/v1/questions/question-forms")
                .param("memberId", "1")
                .param("coupleId", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("create-question-form-and-question-by-user",
                responseHeaders(
                    headerWithName("Location").description("리소스 저장 경로")
                ),
                queryParameters(
                    parameterWithName("memberId").description("사용자 ID"),
                    parameterWithName("coupleId").description("커플 ID")
                ),
                requestFields(
                    fieldWithPath("questionContent").type(STRING).description("질문 내용"),
                    fieldWithPath("firstChoice").type(STRING).description("첫 번째 선택지"),
                    fieldWithPath("secondChoice").type(STRING).description("두 번째 선택지"),
                    fieldWithPath("thirdChoice").type(STRING).description("세 번째 선택지"),
                    fieldWithPath("fourthChoice").type(STRING).description("네 번째 선택지")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.questionId").type(NUMBER).description("질문 ID"),
                    fieldWithPath("body.questionContent").type(STRING).description("질문 내용"),
                    fieldWithPath("body.firstChoice").type(STRING).description("첫 번째 선택지"),
                    fieldWithPath("body.secondChoice").type(STRING).description("두 번째 선택지"),
                    fieldWithPath("body.thirdChoice").type(STRING).description("세 번째 선택지"),
                    fieldWithPath("body.fourthChoice").type(STRING).description("네 번째 선택지"),
                    fieldWithPath("body.questionFormType").type(STRING).description("질문의 타입 (서버인지 커스텀인지)"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }


    @DisplayName("질문을 생성하는 API")
    @Test
    void createQuestion() throws Exception {
        CreateQuestionResponse mockResponse = new CreateQuestionResponse(
            1L, "테스트 질문", "선택지 1", "선택지 2", null, null, QuestionFormType.SERVER
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
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.questionId").type(NUMBER).description("질문 ID"),
                    fieldWithPath("body.questionContent").type(STRING).description("질문 내용"),
                    fieldWithPath("body.firstChoice").type(STRING).description("첫 번째 선택지"),
                    fieldWithPath("body.secondChoice").type(STRING).description("두 번째 선택지"),
                    fieldWithPath("body.thirdChoice").type(STRING).optional().description("세 번째 선택지"),
                    fieldWithPath("body.fourthChoice").type(STRING).optional().description("네 번째 선택지"),
                    fieldWithPath("body.questionFormType").type(STRING).description("질문의 타입 (서버인지 커스텀인지)"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }


    @DisplayName("질문에 답변하는 API")
    @Test
    void answerQuestion() throws Exception {
        // questionService.updateQuestionAnswer()가 호출되면 아무런 동작도 하지 않도록 설정
        willDoNothing().given(questionService).updateQuestionAnswer(1L, Sex.FEMALE, 1);
        AnswerQuestionRequest request = new AnswerQuestionRequest(1);

        mockMvc.perform(patch("/v1/questions/{id}/answers", 1L)
                .queryParam("sex", "MALE")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("answer-question",
                queryParameters(
                    parameterWithName("sex").description("성별")
                ),
                pathParameters(
                    parameterWithName("id").description("질문 ID")
                ),
                requestFields(
                    fieldWithPath("choiceNumber").type(NUMBER).description("선택한 답변의 번호")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body").type(NULL).description("응답 바디"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }


    @DisplayName("질문 상세 정보를 가져오는 API")
    @Test
    void getQuestionDetails() throws Exception {
        // questionService.findQuestionDetails()가 호출되면 미리 정의된 QuestionDetailsResponse 객체를 반환하도록 설정
        QuestionDetailsResponse mockResponse = new QuestionDetailsResponse("테스트 질문", "내 답변", "상대의 답변", 1, 2, "내 프로필", "상대의 프로필");
        given(questionQueryService.findQuestionDetails(1L, 1L, Sex.MALE)).willReturn(mockResponse);

        mockMvc.perform(get("/v1/questions/details/{id}", 1L)
                .queryParam("memberId", "1")
                .queryParam("sex", "MALE")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-question-details",
                queryParameters(
                    parameterWithName("memberId").description("회원의 아이디"),
                    parameterWithName("sex").description("회원의 성별")
                ),
                pathParameters(
                    parameterWithName("id").description("질문 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.questionContent").type(STRING).description("질문 내용"),
                    fieldWithPath("body.myAnswer").type(STRING).description("나의 답변"),
                    fieldWithPath("body.opponentAnswer").type(STRING).description("상대방의 답변"),
                    fieldWithPath("body.myChoiceIndex").type(NUMBER).description("내가 고른 번호"),
                    fieldWithPath("body.opponentChoiceIndex").type(NUMBER).description("상대방이 고른 번호"),
                    fieldWithPath("body.myProfile").type(STRING).description("나의 프로필"),
                    fieldWithPath("body.opponentProfile").type(STRING).description("상대방의 프로필"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }

    @DisplayName("커플이 대답한 질문의 목록을 가져오는 API")
    @Test
    void getAnsweredQuestions() throws Exception {
        QuestionChoices questionChoices = QuestionChoices.create("test1", "test2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "test", questionChoices, 0L, QuestionFormType.SERVER);
        Question question = mock(Question.class);
        given(question.getId()).willReturn(1L);
        given(question.getQuestionForm()).willReturn(questionForm);
        List<AnsweredQuestionResponse.QuestionResponse> mockResponse = List.of(
            AnsweredQuestionResponse.QuestionResponse.from(question)
        );
        AnsweredQuestionResponse response = new AnsweredQuestionResponse(mockResponse);
        given(questionService.findAllAnsweredQuestionByCoupleId(anyLong(), anyLong(), anyInt())).willReturn(response);

        mockMvc.perform(get("/v1/questions")
                .param("id", "1")
                .param("coupleId", "1")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-answered-questions",
                queryParameters(
                    parameterWithName("id").description("조회를 시작할 id, default: 0").optional(),
                    parameterWithName("coupleId").description("조회 할 커플의 id"),
                    parameterWithName("limit").description("조회할 개수 default: 10").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("응답 코드"),
                    fieldWithPath("body.answeredQuestions[]").type(ARRAY).description("커플이 대답한 질문 목록"),
                    fieldWithPath("body.answeredQuestions[].questionId").type(NUMBER).description("질문의 ID"),
                    fieldWithPath("body.answeredQuestions[].questionContent").type(STRING).description("질문 내용"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )

            ));
    }

    @DisplayName("질문 삭제, 커스텀 질문지 삭제하는 api docs")
    @Test
    void init() throws Exception {
        mockMvc.perform(delete("/v1/questions")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(print())
            .andDo(document("delete-custom-questions-and-questionForm"));
    }

}