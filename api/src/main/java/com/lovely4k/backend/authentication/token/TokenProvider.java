package com.lovely4k.backend.authentication.token;

import com.lovely4k.backend.authentication.RefreshToken;
import com.lovely4k.backend.authentication.RefreshTokenRepository;
import com.lovely4k.backend.member.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
//    private static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30 * 2 * 24;            // 1일
    private static final int REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;     //7일

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
            .setSubject(member.getEmail())
            .claim(AUTHORITIES_KEY, member.getRole().toString())
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        RefreshToken refreshTokenObject = RefreshToken.builder()
            .id(String.valueOf(member.getId()))
            .member(member)
            .keyValue(refreshToken)
            .build();
        refreshTokenRepository.save(refreshTokenObject);

        return new TokenDto(BEARER_PREFIX, accessToken, refreshToken, accessTokenExpiresIn.getTime());
    }

    public String generateAccessToken(Member member) {
        long now = (new Date().getTime());
        return Jwts.builder()
            .setSubject(member.getEmail())
            .claim(AUTHORITIES_KEY, member.getRole().toString())
            .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    @Transactional(readOnly = true)
    public RefreshToken findRefreshTokenByKeyValue(String keyValue) {
        return refreshTokenRepository.findByKeyValue(keyValue)
            .orElseThrow(() -> new IllegalArgumentException("잘못된 리프레시 토큰 입니다."));
    }

}