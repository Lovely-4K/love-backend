package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("회원 프로필을 조회한다.")
    void getMemberProfile() throws Exception {
        //given
        Long memberId = 1L;

        given(memberService.getMemberProfile(memberId))
            .willReturn(new MemberProfileGetResponse(
                    "boy",
                    "sampleImageUrl",
                    "김철수",
                    "듬직이",
                    LocalDate.of(1996, 7, 30),
                    "ESFJ",
                    "white"
                )
            );

        //when //then
        mockMvc.perform(get("/v1/members")
                .queryParam("userId", "1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.body.sex").value("boy"))
            .andExpect(jsonPath("$.body.name").value("김철수"));
    }

    @Test
    @DisplayName("회원 프로필을 수정한다.")
    void editMemberProfile() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "MALE",
            "http://www.imageUrlSample.com",
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body").isEmpty());
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 성별이 반드시 입력되어야 한다.")
    void editMemberProfileWithoutSex() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            null,
            "http://www.imageUrlSample.com",
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 이미지 주소가 반드시 입력되어야 한다.")
    void editMemberProfileWithoutImageUrl() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "MALE",
            null,
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 이름이 반드시 입력되어야 한다.")
    void editMemberProfileWithout() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "MALE",
            "http://www.imageUrlSample.com",
            null,
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("회원 프로필을 수정할 경우 별명이 반드시 입력되어야 한다.")
    void editMemberProfileWithoutNickname() throws Exception {
        //given
        MemberProfileEditRequest request = new MemberProfileEditRequest(
            "MALE",
            "http://www.imageUrlSample.com",
            "김동수",
            null,
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
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
            "MALE",
            "http://www.imageUrlSample.com",
            "김동수",
            "길쭉이",
            null,
            "ENFP",
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
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
            "MALE",
            "http://www.imageUrlSample.com",
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            null,
            "blue"
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
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
            "MALE",
            "http://www.imageUrlSample.com",
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            null
        );

        //when //then
        mockMvc.perform(
                patch("/v1/members")
                    .queryParam("userId", "1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }
}