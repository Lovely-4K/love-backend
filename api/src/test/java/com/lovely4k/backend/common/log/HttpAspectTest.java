package com.lovely4k.backend.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HttpAspectTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private HttpAspect httpAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
    }

    @Test
    void printLog() throws Throwable {
        // given
        given(joinPoint.proceed()).willReturn(null);
        given(objectMapper.writeValueAsString(any())).willReturn("log message");

        // when
        Object result = httpAspect.printLog(joinPoint);

        // then
        then(joinPoint).should(times(1)).proceed();
        then(objectMapper).should(atLeastOnce()).writeValueAsString(any());
        assertThat(result).isNull();
    }

    @Test
    void printErrorLog() throws Throwable {
        // given
        given(joinPoint.proceed()).willReturn(null);
        given(objectMapper.writeValueAsString(any())).willReturn("error log message");

        // when
        Object result = httpAspect.printErrorLog(joinPoint);

        // then
        then(joinPoint).should(times(1)).proceed();
        then(objectMapper).should(atLeastOnce()).writeValueAsString(any());
        assertThat(result).isNull();
    }
}