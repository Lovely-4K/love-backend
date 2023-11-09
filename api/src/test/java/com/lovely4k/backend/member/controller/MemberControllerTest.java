package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("회원 프로필을 조회한다.")
    void getMemberProfile() throws Exception {
        //given
        Long memberId = 1L;

        given(memberService.findMemberProfile(memberId))
            .willReturn(new MemberProfileGetResponse(
                    "sampleImageUrl",
                    "듬직이",
                    LocalDate.of(1996, 7, 30),
                    "ESFJ",
                    "white"
                )
            );

        //when //then
        mockMvc.perform(get("/v1/members"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.body.mbti").value("ESFJ"))
            .andExpect(jsonPath("$.body.nickname").value("듬직이"));
    }

    @Test
    @DisplayName("회원 프로필을 수정한다.")
    void editMemberProfile() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        MockMultipartFile images = new MockMultipartFile("images", "profileImage.png", "image/png", "profileImage data".getBytes());
        MockMultipartFile texts = new MockMultipartFile("texts", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        //when //then
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/v1/members")
                    .file(images)
                    .file(texts)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body").isEmpty());
    }


    @Test
    @DisplayName("회원 프로필을 수정할 경우 별명이 반드시 입력되어야 한다.")
    void editMemberProfileWithoutNickname() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            null,
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        MockMultipartFile images = new MockMultipartFile("images", "profileImage.png", "image/png", "profileImage data".getBytes());
        MockMultipartFile texts = new MockMultipartFile("texts", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        //when //then
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/v1/members")
                    .file(images)
                    .file(texts)
                    .queryParam("memberId", "1")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 생일이 반드시 입력되어야 한다.")
    void editMemberProfileWithoutBirthday() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "길쭉이",
            null,
            "ENFP",
            "blue"
        );

        MockMultipartFile images = new MockMultipartFile("images", "profileImage.png", "image/png", "profileImage data".getBytes());
        MockMultipartFile texts = new MockMultipartFile("texts", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        //when //then
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/v1/members")
                    .file(images)
                    .file(texts)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 MBTI가 반드시 입력되어야 한다.")
    void editMemberProfileWithoutMBTI() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            null,
            "blue"
        );

        MockMultipartFile images = new MockMultipartFile("images", "profileImage.png", "image/png", "profileImage data".getBytes());
        MockMultipartFile texts = new MockMultipartFile("texts", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        //when //then
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/v1/members")
                    .file(images)
                    .file(texts)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 개인 달력 색상이 반드시 입력되어야 한다.")
    void editMemberProfileWithoutColor() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            null
        );

        MockMultipartFile images = new MockMultipartFile("images", "profileImage.png", "image/png", "profileImage data".getBytes());
        MockMultipartFile texts = new MockMultipartFile("texts", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        //when //then
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/v1/members")
                    .file(images)
                    .file(texts)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }
}
