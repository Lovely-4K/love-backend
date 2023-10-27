package com.lovely4k.backend.couple.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.couple.controller.request.TestCoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.*;
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

        given(coupleService.findCoupleProfile(coupleId))
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

    @Test
    @DisplayName("커플 프로필을 수정할 수 있다.")
    void editCoupleProfile() throws Exception {
        //given
        TestCoupleProfileEditRequest request = new TestCoupleProfileEditRequest("2022-07-26");

        //when //then
        mockMvc.perform(patch("/v1/couples")
                .queryParam("memberId", "1")
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
                .queryParam("memberId", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.body.title").value("DateTimeParseException"));
    }
}
