package com.lovely4k.backend.authentication.trial_login;

import com.lovely4k.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/members", produces = MediaTypes.HAL_JSON_VALUE)
public class LoginController {

    private final LoginService loginService;

    @SneakyThrows
    @GetMapping(path = "/trial")
    public ResponseEntity<ApiResponse<LoginResponse>> trialLogin(
    ) {
        long trialMemberId = 200395L;
        return ApiResponse.ok(loginService.trialLogin(trialMemberId));
    }
}
