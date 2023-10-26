package com.lovely4k.backend.couple.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
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
}