package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.authentication.token.UserDetailsImpl;
import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.common.sessionuser.LoginUser;
import com.lovely4k.backend.common.sessionuser.SessionUser;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/members", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {

    private final MemberService memberService;

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<MemberProfileGetResponse>> getMemberProfile(@LoginUser SessionUser sessionUser) {
        return ApiResponse.ok(memberService.findMemberProfile(sessionUser.memberId()),
            linkTo(methodOn(MemberController.class).getMemberProfile(sessionUser)).withSelfRel(),
            linkTo(MemberController.class.getMethod("editMemberProfile", List.class, MemberProfileEditRequest.class, SessionUser.class)).withRel("edit member profile")
        );
    }

    @SneakyThrows
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> editMemberProfile(
        @RequestPart(value = "images", required = false) List<MultipartFile> profileImage,
        @RequestPart(value = "texts") @Valid MemberProfileEditRequest request,
        @LoginUser SessionUser sessionUser) {

        log.debug("profileImage null check : {}", profileImage == null);

        memberService.updateMemberProfile(profileImage, request.toServiceRequest(), sessionUser.memberId());

        return ApiResponse.ok(
            linkTo(methodOn(MemberController.class).editMemberProfile(profileImage, request, sessionUser)).withSelfRel(),
            linkTo(MemberController.class.getMethod("getMemberProfile", SessionUser.class)).withRel("get member profile")
        );
    }
}