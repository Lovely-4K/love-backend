package com.lovely4k.backend.authentication.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.Key;
import java.util.Map;

@Slf4j
public class InvalidateTokenResponseWriter {

    public static void write(Key key, String token, HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰!!!!!!");
            expiredTokenResponse(response, objectMapper, e);
            throw new IllegalArgumentException("만료된 토큰");
        } catch (Exception e) {
            log.debug("잘못된 JWT 토큰!!!!!!!");
            invalidTokenResponse(response, objectMapper, e);
            throw new IllegalArgumentException("잘못된 토큰");
        }
    }

    private static void expiredTokenResponse(HttpServletResponse response, ObjectMapper objectMapper, Exception e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println(
            objectMapper.writeValueAsString(
                Map.of("예외", "토큰 만료", "메시지", e.getMessage())
            )
        );
    }

    private static void invalidTokenResponse(HttpServletResponse response, ObjectMapper objectMapper, Exception e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println(
            objectMapper.writeValueAsString(
                Map.of("예외", "잘못된 토큰", "메시지", e.getMessage())
            )
        );
    }

}