package com.lovely4k.backend.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public record ApiResponse<T>(
    int code,
    T body
) {

    public static <T> ResponseEntity<ApiResponse<T>> created(String requestURI, Long resourceId) {
        return ResponseEntity.created(URI.create(requestURI + "/" + resourceId))
            .body(new ApiResponse<>(HttpStatus.CREATED.value(), null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String requestURI, Long resourceId, T body) {
        return ResponseEntity.created(URI.create(requestURI + "/" + resourceId))
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), body));
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(HttpStatus httpStatus, T body) {
        return new ResponseEntity<>(new ApiResponse<>(httpStatus.value(), body), httpStatus);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T body) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), body), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok() {
        return new ResponseEntity<>(new ApiResponse<>(200, null), HttpStatus.OK);
    }
}