package com.lovely4k.backend.couple.controller;


import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.couple.controller.request.CoupleProfileEditRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/couples")
public class CoupleController {

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
