package com.lovely4k.docs.couple;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lovely4k.backend.couple.controller.CoupleController;
import com.lovely4k.backend.couple.controller.request.CoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoupleControllerDocsTest extends RestDocsSupport {

    private final CoupleService coupleService = mock(CoupleService.class);

    @Override
    protected Object initController() {
        return new CoupleController(coupleService);
    }

    @Test
    @DisplayName("커플을 등록하는 API")
    void registerCouple() throws Exception {
        mockMvc.perform(
                post("/v1/couples")
                    .param("invitationCode", "invitationCodeSample")
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("couple-register",
                    preprocessResponse(prettyPrint()),
                    responseHeaders(headerWithName("Location").description("리소스 저장 경로")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("응답 코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL)
                            .description("응답 바디")
                    )
                )
            );
    }

    @Test
    @DisplayName("커플 프로필을 조회하는 API")
    void getCoupleProfile() throws Exception {
        mockMvc.perform(
                get("/v1/couples")
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("couple-profile-get",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("응답 코드"),
                        fieldWithPath("body.boyNickname").type(JsonFieldType.STRING)
                            .description("남자친구 별명"),
                        fieldWithPath("body.boyMbti").type(JsonFieldType.STRING)
                            .description("남자친구 MBTI"),
                        fieldWithPath("body.girlNickname").type(JsonFieldType.STRING)
                            .description("여자친구 별명"),
                        fieldWithPath("body.girlMbti").type(JsonFieldType.STRING)
                            .description("여자친구 MBTI")
                    )
                )
            );
    }

    @Test
    @DisplayName("커플 프로필의 만난날을 수정하는 API")
    void editProfile() throws Exception {
        CoupleProfileEditRequest request =
            new CoupleProfileEditRequest(LocalDate.of(2022, 7, 26));

        mockMvc.perform(
                patch("/v1/couples")
                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("couple-profile-edit",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("meetDay").type(JsonFieldType.ARRAY)
                            .description("만난날")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("응답 코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL)
                            .description("응답 바디")
                    )
                )
            );
    }

}