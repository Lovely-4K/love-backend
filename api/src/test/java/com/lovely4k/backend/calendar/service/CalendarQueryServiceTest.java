package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.CalendarQueryRepository;
import com.lovely4k.backend.calendar.repository.FindCalendarsWithDateRepositoryRequest;
import com.lovely4k.backend.calendar.repository.response.FindCalendarsWithDateResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import com.lovely4k.backend.calendar.service.response.FindCalendarsWithDateServiceResponse;
import com.lovely4k.backend.calendar.service.response.FindRecentCalendarsServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CalendarQueryServiceTest {

    @Mock
    CalendarQueryRepository repository;

    @InjectMocks
    CalendarQueryService calendarQueryService;

    @DisplayName("최근 커플의 최근 일정을 limit 만큼 조회할 수 있다.")
    @Test
    void findRecentCalendars() {
        // given
        long coupleId = 1L;
        int limit = 5;
        long loginUserId= 1L;
        FindRecentCalendarsResponse mockResponse = new FindRecentCalendarsResponse(
            1L, 1L, "RED", 2L, "BLUE",
            LocalDate.of(2023, 11, 4),
            LocalDate.of(2023, 11, 5),
            "영화보기",
            ScheduleType.DATE,
            1L
        );
        List<FindRecentCalendarsResponse> mockResponseList = Collections.singletonList(mockResponse);
        given(repository.findRecentCalendarsWithColors(anyLong(), anyInt(), anyLong())).willReturn(mockResponseList);

        FindRecentCalendarsServiceResponse expectedResponse = FindRecentCalendarsServiceResponse.from(mockResponseList);

        // when
        FindRecentCalendarsServiceResponse actualResponse = calendarQueryService.findRecentCalendars(coupleId, limit, loginUserId);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("특정 날짜 범위에 해당하는 일정을 조회할 수 있다.")
    @Test
    void findCalendarsWithDate() {
        // given
        FindCalendarsWithDateRepositoryRequest mockRequest = new FindCalendarsWithDateRepositoryRequest(
            LocalDate.of(2023, 11, 1),
            LocalDate.of(2023, 11, 30)
        );
        FindCalendarsWithDateResponse mockResponse = new FindCalendarsWithDateResponse(
            1L, 1L, "RED", 2L, "BLUE",
            LocalDate.of(2023, 11, 4),
            LocalDate.of(2023, 11, 5),
            "영화보기",
            ScheduleType.DATE,
            1L
        );
        List<FindCalendarsWithDateResponse> mockResponseList = Collections.singletonList(mockResponse);
        given(repository.findCalendarsWithDate(any(), any(), any())).willReturn(mockResponseList);

        FindCalendarsWithDateServiceResponse expectedResponse = FindCalendarsWithDateServiceResponse.from(mockResponseList);

        // when
        FindCalendarsWithDateServiceResponse actualResponse = calendarQueryService.findCalendarsWithDate(mockRequest, 1L, 1L);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}