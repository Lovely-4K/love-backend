package com.lovely4k.backend.calendar.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.calendar.controller.request.CreateCalendarRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CalendarControllerTest extends ControllerTestSupport {

    @DisplayName("일정을 잘못된 값으로 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("provideInvalidCreateCalendarRequests")
    void createScheduleWithInvalidRequest(CreateCalendarRequest request) throws Exception {
        mockMvc.perform(
                        post("/v1/calendars")
                                .content(objectMapper.writeValueAsString(request))
                                .param("coupleId", "1")
                                .param("memberId", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    static Stream<Arguments> provideInvalidCreateCalendarRequests() {
        return Stream.of(
                Arguments.of(new CreateCalendarRequest(null, LocalDate.now(), "Details", "TRAVEL")),
                Arguments.of(new CreateCalendarRequest(LocalDate.now(), null, "Details", "TRAVEL")),
                Arguments.of(new CreateCalendarRequest(LocalDate.now(), LocalDate.now(), "", "TRAVEL")),
                Arguments.of(new CreateCalendarRequest(LocalDate.now(), LocalDate.now(), "Details", "INVALID_TYPE"))
        );
    }

}