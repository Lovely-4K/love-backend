package com.lovely4k.backend.couple.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.couple.CoupleStatus;
import com.lovely4k.backend.couple.controller.request.TestCoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Sex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoupleControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("초대 코드를 발급받는다.")
    void createInvitationCode() throws Exception {
        //given
        Long requestedMemberId = 1L;
        Sex sex = Sex.MALE;
        Long coupleId = 2L;
        String invitationCode = UUID.randomUUID().toString();

        given(coupleService.createInvitationCode(requestedMemberId, sex.name()))
            .willReturn(new InvitationCodeCreateResponse(coupleId, invitationCode));

        //when //then
        mockMvc.perform(post("/v1/couples/invitation-code"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.body.coupleId").value(coupleId))
            .andExpect(jsonPath("$.body.invitationCode").value(invitationCode));
    }

    @Test
    @DisplayName("초대코드와 초대코드를 받은 회원의 id를 통해 커플을 등록할 수 있다.")
    void registerCouple() throws Exception {
        //given
        String invitationCode = UUID.randomUUID().toString();

        //when //then
        mockMvc.perform(post("/v1/couples")
                .queryParam(
                    "invitationCode", invitationCode)
                .contentType(APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("coupleId를 통해서 커플 프로필을 조회할 수 있다.")
    void getCoupleProfile() throws Exception {
        //given
        Long memberId = 1L;

        given(coupleService.findCoupleProfile(memberId)).willReturn(
            new CoupleProfileGetResponse(
                "듬직이",
                "ESTJ",
                "boyProfileUrl",
                1L,
                "#FF5733",
                LocalDate.of(1995, 5, 15),
                "깜찍이",
                "INFP",
                "girlProfileUrl",
                2L, // opponentId
                LocalDate.of(1995, 10, 21),
                "#C70039",
                LocalDate.of(2020, 7, 23),
                CoupleStatus.RELATIONSHIP
            )
        );

        //when //then
        mockMvc.perform(get("/v1/couples"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.myNickname").value("듬직이"))
            .andExpect(jsonPath("$.body.myMbti").value("ESTJ"))
            .andExpect(jsonPath("$.body.opponentNickname").value("깜찍이"))
            .andExpect(jsonPath("$.body.opponentMbti").value("INFP"));
    }

    @Test
    @DisplayName("커플 프로필을 수정할 수 있다.")
    void editCoupleProfile() throws Exception {
        //given
        TestCoupleProfileEditRequest request = new TestCoupleProfileEditRequest("2022-07-26");

        //when //then
        mockMvc.perform(patch("/v1/couples")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("커플 프로필을 수정시 날짜는 yyyy-MM-dd 패턴으로 입력되어야 한다.")
    void editCoupleProfileWithWrongDatePattern() throws Exception {
        //given
        TestCoupleProfileEditRequest request = new TestCoupleProfileEditRequest("2022-7-26");

        //when //then
        mockMvc.perform(patch("/v1/couples")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.body.title").exists());
    }

}
