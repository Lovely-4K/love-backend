package com.lovely4k.backend.calendar.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.backend.calendar.controller.request.CreateCalendarRequest;
import com.lovely4k.backend.calendar.controller.request.FindCalendarsWithDateRequest;
import com.lovely4k.backend.calendar.controller.request.UpdateCalendarRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @DisplayName("일정을 잘못된 값으로 업데이트 할 수 없다.")
    @ParameterizedTest
    @MethodSource("provideInvalidUpdateCalendarRequests")
    void editScheduleById(UpdateCalendarRequest request) throws Exception {
        mockMvc.perform(
                        patch("/v1/calendars/{id}", 1L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    static Stream<Arguments> provideInvalidUpdateCalendarRequests() {
        return Stream.of(
                Arguments.of(new UpdateCalendarRequest(null, LocalDate.now(), "Details", "TRAVEL")),
                Arguments.of(new UpdateCalendarRequest(LocalDate.now(), null, "Details", "TRAVEL")),
                Arguments.of(new UpdateCalendarRequest(LocalDate.now(), LocalDate.now(), "", "TRAVEL")),
                Arguments.of(new UpdateCalendarRequest(LocalDate.now(), LocalDate.now(), "Details", "INVALID_TYPE"))
        );
    }

    @DisplayName("잘못된 날짜 범위로 일정을 조회할 수 없다.")
    @ParameterizedTest
    @MethodSource("provideInvalidFindCalendarsWithDateRequests")
    void findAllSchedulesWithInvalidDateRange(FindCalendarsWithDateRequest request) throws Exception {
        mockMvc.perform(
                get("/v1/calendars")
                    .param("from", Optional.ofNullable(request.from()).map(LocalDate::toString).orElse(null))
                    .param("to", Optional.ofNullable(request.to()).map(LocalDate::toString).orElse(null))
                    .param("coupleId", String.valueOf(request.coupleId()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"));
    }

    static Stream<Arguments> provideInvalidFindCalendarsWithDateRequests() {
        LocalDate now = LocalDate.now();
        return Stream.of(
            Arguments.of(new FindCalendarsWithDateRequest(null, now, 1L)), // 'from' is null
            Arguments.of(new FindCalendarsWithDateRequest(now, null, 1L)) // 'to' is null
        );
    }

}