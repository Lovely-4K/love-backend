package com.lovely4k.backend.common.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static jakarta.servlet.RequestDispatcher.*;

@RequiredArgsConstructor
@RestController
public class OutsideDispatcherErrorHandler implements ErrorController {

    @GetMapping(value = "/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        int status = (int) request.getAttribute(ERROR_STATUS_CODE);
        Throwable throwable = (Throwable) request.getAttribute(ERROR_EXCEPTION);
        String exceptionMessage = throwable != null ? throwable.getMessage() : "null";

        Map<String, Object> errorAttributes = Map.of(
            "exceptionType", throwable != null ? throwable.getClass().getName() : "null",
            "message", exceptionMessage,
            "path", request.getAttribute(ERROR_REQUEST_URI),
            "servletName", request.getAttribute(ERROR_SERVLET_NAME),
            "statusCode", status,
            "dispatchType", request.getDispatcherType().toString()
        );

        throw new ResponseStatusException(HttpStatusCode.valueOf(status), errorAttributes.toString());
    }
}