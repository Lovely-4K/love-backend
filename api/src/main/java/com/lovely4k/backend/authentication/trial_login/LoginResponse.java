package com.lovely4k.backend.authentication.trial_login;

import com.lovely4k.backend.authentication.token.TokenDto;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {
    public static LoginResponse of(TokenDto tokenDto) {
        return new LoginResponse(tokenDto.accessToken(), tokenDto.refreshToken());
    }
}
