package com.lovely4k.backend.authentication.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovely4k.backend.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerException implements AccessDeniedHandler {  // NOSONAR

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ResponseEntity<ApiResponse<String>> responseEntity = ApiResponse.fail(HttpStatus.FORBIDDEN, "ACCESS DENIED, 접근거부: 권한이 없습니다.");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity));
    }
}