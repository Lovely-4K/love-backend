package com.lovely4k.backend.couple.controller;


import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.couple.controller.request.CoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Sex;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/couples", produces = MediaTypes.HAL_JSON_VALUE)
public class CoupleController {

    private final CoupleService coupleService;

    @SneakyThrows
    @PostMapping(value = "/invitation-code")
    public ResponseEntity<ApiResponse<InvitationCodeCreateResponse>> createInvitationCode(@RequestParam Long requestedMemberId, @RequestParam Sex sex) {
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(requestedMemberId, sex);

        return ApiResponse.created(response, response.coupleId(),
                linkTo(methodOn(CoupleController.class).createInvitationCode(requestedMemberId, sex)).withSelfRel(),
                linkTo(CoupleController.class.getMethod("registerCouple", String.class, Long.class)).withRel("register couple"));
    }

    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> registerCouple(@RequestParam String invitationCode, @RequestParam Long receivedMemberId) {
        coupleService.registerCouple(invitationCode, receivedMemberId);

        return ApiResponse.ok(
                linkTo(methodOn(CoupleController.class).registerCouple(invitationCode, receivedMemberId)).withSelfRel(),
                linkTo(CoupleController.class.getMethod("getCoupleProfile", Long.class)).withRel("get couple profile")
        );
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<CoupleProfileGetResponse>> getCoupleProfile(@RequestParam Long memberId) {

        return ApiResponse.ok(coupleService.findCoupleProfile(memberId),
                linkTo(methodOn(CoupleController.class).getCoupleProfile(memberId)).withSelfRel(),
                linkTo(CoupleController.class.getMethod("editCoupleProfile", CoupleProfileEditRequest.class, Long.class)).withRel("edit couple profile"));
    }

    @SneakyThrows
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> editCoupleProfile(@Valid @RequestBody CoupleProfileEditRequest request, @RequestParam Long memberId) {
        coupleService.updateCoupleProfile(request.toServiceRequest(), memberId);

        return ApiResponse.ok(
                linkTo(methodOn(CoupleController.class).editCoupleProfile(request, memberId)).withSelfRel(),
                linkTo(CoupleController.class.getMethod("getCoupleProfile", Long.class)).withRel("get couple profile")
        );
    }

    @DeleteMapping("/{coupleId}")
    public ResponseEntity<Void> deleteCouple(
            @PathVariable Long coupleId,
            @RequestParam Long memberId) {

        return ResponseEntity.noContent().build();
    }
}
