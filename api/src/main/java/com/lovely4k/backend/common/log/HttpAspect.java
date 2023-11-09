package com.lovely4k.backend.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class HttpAspect {

    private final ObjectMapper objectMapper;

    @Value("${love.system.max-affordable-time}")
    private double maxAffordableTime;

    private static final double MILLI_SECOND_TO_SECOND_UNIT = 0.001;

    /*
     * Controller Log
     */
    @SneakyThrows
    @Around("com.lovely4k.backend.common.log.Pointcuts.allController()")
    public Object printLog(ProceedingJoinPoint joinPoint) {

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) * MILLI_SECOND_TO_SECOND_UNIT;

        HttpServletResponse response = sra.getResponse();
        HttpLogResponse httpLog = HttpLogResponse.of(request, Objects.requireNonNull(response), objectMapper, proceed);

        if (elapsedTime > maxAffordableTime) {
            log.warn(objectMapper.writeValueAsString(httpLog));
            return proceed;
        }

        log.info(objectMapper.writeValueAsString(httpLog.removeBody()));

        return proceed;
    }

    /*
     * RestControllerAdvice Log
     */
    @SneakyThrows
    @Around("com.lovely4k.backend.common.log.Pointcuts.globalExceptionHandler()")
    public Object printErrorLog(ProceedingJoinPoint joinPoint) {

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = sra.getRequest();
        Object proceed = joinPoint.proceed();
        HttpServletResponse response = sra.getResponse();

        log.warn(objectMapper.writeValueAsString(HttpLogResponse.of(request, Objects.requireNonNull(response), objectMapper, proceed)));

        return proceed;
    }
}