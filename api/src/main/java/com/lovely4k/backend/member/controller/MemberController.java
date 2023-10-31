package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
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
@RequestMapping(value = "/v1/members", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {

    private final MemberService memberService;

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<MemberProfileGetResponse>> getMemberProfile(@RequestParam Long memberId) {

        return ApiResponse.ok(memberService.findMemberProfile(memberId),
            linkTo(methodOn(MemberController.class).getMemberProfile(memberId)).withSelfRel(),
            linkTo(MemberController.class.getMethod("editMemberProfile", MemberProfileEditRequest.class, Long.class)).withRel("edit member profile")
        );
    }

    @SneakyThrows
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> editMemberProfile(@Valid @RequestBody MemberProfileEditRequest request,
                                                               @RequestParam Long memberId) {
        memberService.updateMemberProfile(request.toServiceRequest(), memberId);

        return ApiResponse.ok(
            linkTo(methodOn(MemberController.class).editMemberProfile(request, memberId)).withSelfRel(),
            linkTo(MemberController.class.getMethod("getMemberProfile", Long.class)).withRel("get member profile")
            );
    }
}
