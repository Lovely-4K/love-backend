package com.lovely4k.backend.member.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.member.controller.request.MemberProfileEditRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class MemberController {

    @GetMapping
    public ResponseEntity<ApiResponse<MemberProfileGetResponse>> getMemberProfile() {

        return ApiResponse.ok(new MemberProfileGetResponse(
            "boy",
            "imageUrlSample",
            "김철수",
            "깜찍이",
            LocalDate.of(1996, 7, 30),
            "ENTP",
            "white"
        ));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Object>> editMemberProfile(@RequestBody MemberProfileEditRequest request) {

        return ApiResponse.ok(null);
    }
}
