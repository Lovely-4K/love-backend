package com.lovely4k.backend.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class RequestWrapperTest {

    @DisplayName("json으로 받은 body를 출력할 때 {key:value} 형식으로 출력된다.")
    @Test
    void parsing_ok() throws Exception {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("{\"key1\":\"value1\",\"key2\":\"value2\"}".getBytes());
        RequestWrapper requestWrapper = new RequestWrapper(mockRequest);
        ObjectMapper objectMapper = new ObjectMapper();

        // When
        String formattedBody = requestWrapper.getBody(objectMapper);

        // Then
        Assertions.assertThat(formattedBody).isEqualTo("key1: value1, key2: value2");
    }

    @DisplayName("json으로 받은 body가 올바르지 않을 때 바디 그대로 출력된다.")
    @Test
    void parsing_fail() throws Exception {
        // Given
        String originalBody = "{\"invalid_json";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent(originalBody.getBytes());
        RequestWrapper requestWrapper = new RequestWrapper(mockRequest);
        ObjectMapper objectMapper = new ObjectMapper();

        // When
        String body = requestWrapper.getBody(objectMapper);

        // Then
        Assertions.assertThat(body).isEqualTo(originalBody);
    }


}
