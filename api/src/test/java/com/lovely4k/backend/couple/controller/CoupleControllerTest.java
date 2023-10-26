package com.lovely4k.backend.couple.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoupleControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("초대 코드를 발급받는다.")
    void createInvitationCode() throws Exception {
        //given
        Long requestedMemberId = 1L;
        Long coupleId = 2L;
        String invitationCode = UUID.randomUUID().toString();

        given(coupleService.createInvitationCode(requestedMemberId))
            .willReturn(new InvitationCodeCreateResponse(coupleId, invitationCode));

        //when //then
        mockMvc.perform(post("/v1/couples/invitation-code")
                .queryParam("requestedMemberId", requestedMemberId.toString()))
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
                .queryParam("invitationCode", invitationCode)
                .queryParam("receivedMemberId", "2")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("coupleId를 통해서 커플 프로필을 조회할 수 있다.")
    void getCoupleProfile() throws Exception {
        //given
        Long coupleId = 1L;

        given(coupleService.getCoupleProfile(coupleId))
            .willReturn(new CoupleProfileGetResponse(
                "듬직이",
                "ESTJ",
                "깜찍이",
                "INFP"
            ));

        //when //then
        mockMvc.perform(get("/v1/couples")
                .queryParam("coupleId", coupleId.toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.boyNickname").value("듬직이"))
            .andExpect(jsonPath("$.body.boyMbti").value("ESTJ"))
            .andExpect(jsonPath("$.body.girlNickname").value("깜찍이"))
            .andExpect(jsonPath("$.body.girlMbti").value("INFP"));
    }
}