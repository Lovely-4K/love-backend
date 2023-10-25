package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<MemberProfileGetResponse>> getMemberProfile(@RequestParam Long userId) {

        return ApiResponse.ok(memberService.getMemberProfile(userId));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Object>> editMemberProfile(@Valid @RequestBody MemberProfileEditRequest request,
                                                                 @RequestParam Long userId) {
        memberService.updateMemberProfile(request.toServiceRequest(), userId);

        return ApiResponse.ok(null);
    }
}
