package com.lovely4k.backend.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T> (
        int code,
        T body
) {
    public static <T> ResponseEntity<ApiResponse<T>> fail(HttpStatus httpStatus, T body) {
        return new ResponseEntity<>(new ApiResponse<>(httpStatus.value(), body), httpStatus);
    }
}
