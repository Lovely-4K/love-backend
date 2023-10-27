package com.lovely4k.backend.couple.controller;


import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.couple.controller.request.CoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/couples")
public class CoupleController {

    private final CoupleService coupleService;

    @PostMapping("/invitation-code")
    public ResponseEntity<ApiResponse<InvitationCodeCreateResponse>> createInvitationCode(@RequestParam Long requestedMemberId) {
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(requestedMemberId);

        return ApiResponse.created("/v1/couples/invitation-code", response.coupleId(), response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> registerCouple(@RequestParam String invitationCode) {
        return ApiResponse.created("/v1/couples", 1L);

    }

    @GetMapping
    public ResponseEntity<ApiResponse<CoupleProfileGetResponse>> getCoupleProfile() {
        return ApiResponse.ok(new CoupleProfileGetResponse(
            "듬직이",
            "ESTJ",
            "깜찍이",
            "INTP"
        ));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Object>> editCoupleProfile(@RequestBody CoupleProfileEditRequest request) {
        return ApiResponse.ok(null);
    }
}
