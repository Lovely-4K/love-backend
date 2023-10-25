package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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


}