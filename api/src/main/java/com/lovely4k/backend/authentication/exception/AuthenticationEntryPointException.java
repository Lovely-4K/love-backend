package com.lovely4k.backend.authentication.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovely4k.backend.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntryPointException implements AuthenticationEntryPoint {    // NOSONAR
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ResponseEntity<ApiResponse<String>> responseEntity = ApiResponse.fail(HttpStatus.UNAUTHORIZED, "AUTHENTICATION FAILED , 인증실패: 로그인이 필요합니다!");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity));
    }
}
