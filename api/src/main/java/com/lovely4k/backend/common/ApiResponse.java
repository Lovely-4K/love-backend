package com.lovely4k.backend.common;


import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public record ApiResponse<T> (
        int code,
        T body,
        Links links
){

    public static <T> ResponseEntity<ApiResponse<T>> created(T body, long resourceId, Link... links) {
        return ResponseEntity.created(createdURI(resourceId))
                .body(new ApiResponse<>(201, body, Links.of(links)));
    }

    private static URI createdURI(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(Long resourceId, Link... links) {
        return ResponseEntity.created(createdURI(resourceId))
                .body(new ApiResponse<>(201, null, Links.of(links)));
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(HttpStatus httpStatus, T body) {
        return new ResponseEntity<>(new ApiResponse<>(httpStatus.value(), body, null), httpStatus);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T body, Link... links) {
        return new ResponseEntity<>(new ApiResponse<>(200, body, Links.of(links)), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(Link... links) {
        return new ResponseEntity<>(new ApiResponse<>(200, null, Links.of(links)), HttpStatus.OK);
    }


}