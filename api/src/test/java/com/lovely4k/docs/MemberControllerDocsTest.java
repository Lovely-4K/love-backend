package com.lovely4k.docs;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lovely4k.backend.member.controller.MemberController;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);

    @Override
    protected Object initController() {
        return new MemberController(memberService);
    }

    @Test
    @DisplayName("회원 프로필을 조회하는 API")
    void getProfile() throws Exception {
        given(memberService.getMemberProfile(any(Long.class)))
            .willReturn(new MemberProfileGetResponse(
                "boy",
                "sampleImageUrl",
                "김철수",
                "듬직이",
                LocalDate.of(1996, 7, 30),
                "ESFJ",
                "white"
            ));

        mockMvc.perform(
                get("/v1/members")
                    .queryParam("userId", "1")
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("member-profile-get",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("응답 코드"),
                        fieldWithPath("body.sex").type(JsonFieldType.STRING)
                            .description("성별"),
                        fieldWithPath("body.imageUrl").type(JsonFieldType.STRING)
                            .description("프로필 사진 url"),
                        fieldWithPath("body.name").type(JsonFieldType.STRING)
                            .description("이름"),
                        fieldWithPath("body.nickname").type(JsonFieldType.STRING)
                            .description("별명"),
                        fieldWithPath("body.birthday").type(JsonFieldType.ARRAY)
                            .description("생년월일"),
                        fieldWithPath("body.mbti").type(JsonFieldType.STRING)
                            .description("MBTI"),
                        fieldWithPath("body.calendarColor").type(JsonFieldType.STRING)
                            .description("개인 색상")
                    )
                )
            );
    }

    @Test
    @DisplayName("회원 프로필을 수정하는 API")
    void editProfile() throws Exception {
        MemberProfileEditRequest request = new MemberProfileEditRequest("girl",
            "imageUrlSample",
            "김영희",
            "이쁜이",
            LocalDate.of(1997, 3, 20),
            "INTP",
            "blue");

        mockMvc.perform(
                patch("/v1/members")
                    .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("member-profile-edit",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("sex").type(JsonFieldType.STRING)
                            .description("성별"),
                        fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                            .description("프로필 사진 url"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("별명"),
                        fieldWithPath("birthday").type(JsonFieldType.ARRAY)
                            .description("생년월일"),
                        fieldWithPath("mbti").type(JsonFieldType.STRING)
                            .description("MBTI"),
                        fieldWithPath("calendarColor").type(JsonFieldType.STRING)
                            .description("개인 색상")
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