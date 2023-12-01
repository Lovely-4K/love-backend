package com.lovely4k.backend.common.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class OutsideDispatcherErrorHandler implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @GetMapping(value = "/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);

        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        int status = (int) errors.get("status");
        Throwable throwable = errorAttributes.getError(webRequest);
        String exception = throwable != null ? throwable.toString() : "null";
        errors.put("exception", exception);

        throw new ResponseStatusException(HttpStatusCode.valueOf(status), errors.toString());
    }
}