package com.lovely4k.docs.member;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lovely4k.backend.member.controller.MemberController;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        given(memberService.findMemberProfile(any()))
            .willReturn(new MemberProfileGetResponse(
                "sampleImageUrl",
                "듬직이",
                LocalDate.of(1996, 7, 30),
                "ESFJ",
                "white"
            ));

        mockMvc.perform(
                get("/v1/members")
                    .characterEncoding("utf-8")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("member-profile-get",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("응답 코드"),
                        fieldWithPath("body.imageUrl").type(JsonFieldType.STRING)
                            .description("프로필 사진 url"),
                        fieldWithPath("body.nickname").type(JsonFieldType.STRING)
                            .description("별명"),
                        fieldWithPath("body.birthday").type(JsonFieldType.STRING)
                            .description("생년월일"),
                        fieldWithPath("body.mbti").type(JsonFieldType.STRING)
                            .description("MBTI"),
                        fieldWithPath("body.calendarColor").type(JsonFieldType.STRING)
                            .description("개인 색상"),
                        fieldWithPath("links[0].rel").type(JsonFieldType.STRING)
                            .description("relation of url"),
                        fieldWithPath("links[0].href").type(JsonFieldType.STRING)
                            .description("url of relation"),
                        fieldWithPath("links[1].rel").type(JsonFieldType.STRING)
                            .description("relation of url"),
                        fieldWithPath("links[1].href").type(JsonFieldType.STRING)
                            .description("url of relation")
                    )
                )
            );
    }

    @Test
    @DisplayName("회원 프로필을 수정하는 API")
    void editProfile() throws Exception {
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "이쁜이",
            LocalDate.of(1997, 3, 20),
            "INTP",
            "blue");

        MockMultipartFile images = new MockMultipartFile("images", "profileImage.png", "image/png", "profileImage data".getBytes());
        MockMultipartFile texts = new MockMultipartFile("texts", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/v1/members")
                    .file(images)
                    .file(texts)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("member-profile-edit",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestPartFields("texts",
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("별명"),
                        fieldWithPath("birthday").type(JsonFieldType.STRING)
                            .description("생년월일"),
                        fieldWithPath("mbti").type(JsonFieldType.STRING)
                            .description("MBTI"),
                        fieldWithPath("calendarColor").type(JsonFieldType.STRING)
                            .description("개인 색상")
                    ),
                    requestParts(
                        partWithName("texts").description("회원 프로필 수정 정보"),
                        partWithName("images").description("수정할 프로필 이미지").optional()
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("응답 코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL)
                            .description("응답 바디"),
                        fieldWithPath("links[0].rel").type(JsonFieldType.STRING)
                            .description("relation of url"),
                        fieldWithPath("links[0].href").type(JsonFieldType.STRING)
                            .description("url of relation"),
                        fieldWithPath("links[1].rel").type(JsonFieldType.STRING)
                            .description("relation of url"),
                        fieldWithPath("links[1].href").type(JsonFieldType.STRING)
                            .description("url of relation")
                    )
                )
            );
    }
}
