package com.lovely4k.backend.authentication.token;

public record TokenDto(
    String grantType,
    String accessToken,
    String refreshToken,
    Long accessTokenExpiresIn
) {
}
