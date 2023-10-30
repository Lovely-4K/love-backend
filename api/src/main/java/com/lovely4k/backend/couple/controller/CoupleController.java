package com.lovely4k.backend.couple.controller;


import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.couple.controller.request.CoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Sex;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/couples")
public class CoupleController {

    private final CoupleService coupleService;

    @PostMapping("/invitation-code")
    public ResponseEntity<ApiResponse<InvitationCodeCreateResponse>> createInvitationCode(@RequestParam Long requestedMemberId, @RequestParam Sex sex) {
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(requestedMemberId, sex);

        return ApiResponse.created("/v1/couples/invitation-code", response.coupleId(), response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerCouple(@RequestParam String invitationCode, @RequestParam Long receivedMemberId) {
        coupleService.registerCouple(invitationCode, receivedMemberId);

        return ApiResponse.ok();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CoupleProfileGetResponse>> getCoupleProfile(@RequestParam Long memberId) {

        return ApiResponse.ok(coupleService.findCoupleProfile(memberId));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> editCoupleProfile(@Valid @RequestBody CoupleProfileEditRequest request, @RequestParam Long memberId) {
        coupleService.updateCoupleProfile(request.toServiceRequest(), memberId);

        return ApiResponse.ok();
    }
}
