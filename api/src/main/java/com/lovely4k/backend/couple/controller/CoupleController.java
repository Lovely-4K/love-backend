package com.lovely4k.backend.couple.controller;


import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.common.sessionuser.LoginUser;
import com.lovely4k.backend.common.sessionuser.SessionUser;
import com.lovely4k.backend.couple.controller.request.CoupleProfileEditRequest;
import com.lovely4k.backend.couple.controller.request.DecideReCoupleRequest;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/couples", produces = MediaTypes.HAL_JSON_VALUE)
public class CoupleController {

    private final CoupleService coupleService;

    @SneakyThrows
    @PostMapping(value = "/invitation-code")
    public ResponseEntity<ApiResponse<InvitationCodeCreateResponse>> createInvitationCode(@LoginUser SessionUser sessionUser) {
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(sessionUser.memberId(), sessionUser.sex());

        return ApiResponse.created(response, response.coupleId(),
            linkTo(methodOn(CoupleController.class).createInvitationCode(sessionUser)).withSelfRel(),
            linkTo(CoupleController.class.getMethod("registerCouple", String.class, SessionUser.class)).withRel("register couple"));
    }

    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> registerCouple(@RequestParam String invitationCode, @LoginUser SessionUser sessionUser) {
        coupleService.registerCouple(invitationCode, sessionUser.memberId());

        return ApiResponse.ok(
            linkTo(methodOn(CoupleController.class).registerCouple(invitationCode, sessionUser)).withSelfRel(),
            linkTo(CoupleController.class.getMethod("getCoupleProfile", SessionUser.class)).withRel("get couple profile")
        );
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<CoupleProfileGetResponse>> getCoupleProfile(@LoginUser SessionUser sessionUser) {

        return ApiResponse.ok(coupleService.findCoupleProfile(sessionUser.memberId()),
            linkTo(methodOn(CoupleController.class).getCoupleProfile(sessionUser)).withSelfRel(),
            linkTo(CoupleController.class.getMethod("editCoupleProfile", CoupleProfileEditRequest.class, SessionUser.class)).withRel("edit couple profile"));
    }

    @SneakyThrows
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> editCoupleProfile(@Valid @RequestBody CoupleProfileEditRequest request, @LoginUser SessionUser sessionUser) {
        coupleService.updateCoupleProfile(request.toServiceRequest(), sessionUser.memberId());

        return ApiResponse.ok(
            linkTo(methodOn(CoupleController.class).editCoupleProfile(request, sessionUser)).withSelfRel(),
            linkTo(CoupleController.class.getMethod("getCoupleProfile", SessionUser.class)).withRel("get couple profile")
        );
    }

    @DeleteMapping("/{coupleId}")
    public ResponseEntity<Void> deleteCouple(
        @PathVariable Long coupleId,
        @RequestParam Long memberId) {
        coupleService.deleteCouple(coupleId, memberId);
        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    @PostMapping("/recouple/{coupleId}")
    public ResponseEntity<ApiResponse<Void>> reCouple(
        @PathVariable Long coupleId,
        @LoginUser SessionUser sessionUser
    ) {
        LocalDate requestedDate = LocalDate.now();
        coupleService.reCouple(requestedDate, coupleId, sessionUser.memberId());

        return ApiResponse.ok(
            linkTo(methodOn(CoupleController.class).reCouple(coupleId, sessionUser)).withSelfRel(),
            linkTo(CoupleController.class.getMethod("getCoupleProfile", SessionUser.class)).withRel("get couple profile"),
            linkTo(CoupleController.class.getMethod("editCoupleProfile", CoupleProfileEditRequest.class, SessionUser.class)).withRel("edit couple profile")
        );
    }

    @SneakyThrows
    @PostMapping(value = "/recouple-decide/{recoveryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> decideReCoupleApproval(
        @PathVariable Long recoveryId,
        @LoginUser SessionUser sessionUser,
        @RequestBody @Valid DecideReCoupleRequest request
    ) {
        return ApiResponse.ok(
            linkTo(methodOn(CoupleController.class).decideReCoupleApproval(recoveryId, sessionUser, request)).withSelfRel(),
            linkTo(CoupleController.class.getMethod("getCoupleProfile", SessionUser.class)).withRel("get couple profile"),
            linkTo(CoupleController.class.getMethod("editCoupleProfile", CoupleProfileEditRequest.class, SessionUser.class)).withRel("edit couple profile")
        );
    }

}