package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.Calendar;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.CalendarCommandRepository;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CalendarCommandServiceTest {

    @Mock
    CalendarCommandRepository calendarCommandRepository;

    @InjectMocks
    CalendarCommandService calendarCommandService;

    @DisplayName("일정을 생성할 수 있다.")
    @Test
    void createCalendar() {
        // Given
        Long coupleId = 1L;
        Long memberId = 1L;
        CreateCalendarServiceReqeust reqeust = new CreateCalendarServiceReqeust(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Meeting",
                "DATE"
        );

        Calendar calendar = mock(Calendar.class);

        given(calendar.getId()).willReturn(1L);
        given(calendar.getStartDate()).willReturn(reqeust.startDate());
        given(calendar.getEndDate()).willReturn(reqeust.endDate());
        given(calendar.getScheduleDetails()).willReturn("details");
        given(calendar.getScheduleType()).willReturn(ScheduleType.DATE);
        given(calendar.getOwnerId()).willReturn(0L);
        CreateCalendarResponse expectedResponse = CreateCalendarResponse.from(calendar);

        given(calendarCommandRepository.save(any(Calendar.class))).willReturn(calendar);

        // When
        CreateCalendarResponse actualResponse = calendarCommandService.createCalendar(coupleId, memberId, reqeust);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}