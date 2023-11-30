package com.lovely4k.backend.calendar.service;

import com.lovely4k.TestData;
import com.lovely4k.backend.calendar.Calendar;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.CalendarCommandRepository;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.calendar.service.request.UpdateCalendarServiceRequest;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import com.lovely4k.backend.calendar.service.response.UpdateCalendarResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        CreateCalendarServiceReqeust request = new CreateCalendarServiceReqeust(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Meeting",
                "DATE"
        );

        Calendar calendar = mock(Calendar.class);

        given(calendar.getId()).willReturn(1L);
        given(calendar.getStartDate()).willReturn(request.startDate());
        given(calendar.getEndDate()).willReturn(request.endDate());
        given(calendar.getScheduleDetails()).willReturn("details");
        given(calendar.getScheduleType()).willReturn(ScheduleType.DATE);
        given(calendar.getOwnerId()).willReturn(0L);
        CreateCalendarResponse expectedResponse = CreateCalendarResponse.from(calendar);

        given(calendarCommandRepository.save(any(Calendar.class))).willReturn(calendar);

        // When
        CreateCalendarResponse actualResponse = calendarCommandService.createCalendar(coupleId, memberId, request);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("일정을 수정할 수 있다.")
    void givenValidCalendarId_whenUpdateCalendarById_thenReturnUpdatedCalendarResponse() {
        // Given
        Long calendarId = 1L;
        Long loginUserId = 1L;
        UpdateCalendarServiceRequest request = new UpdateCalendarServiceRequest(
                LocalDate.of(2023, 11, 3),
                LocalDate.of(2023, 11, 10),
                "details",
                "DATE"
        );
        Calendar calendar = TestData.calendar(1L, 2L);

        given(calendarCommandRepository.findById(calendarId)).willReturn(Optional.of(calendar));

        // When
        UpdateCalendarResponse response = calendarCommandService.updateCalendarById(calendarId, request, loginUserId);

        // Then
        assertThat(response).isNotNull()
                .extracting(UpdateCalendarResponse::startDate, UpdateCalendarResponse::endDate, UpdateCalendarResponse::scheduleDetails, UpdateCalendarResponse::scheduleType)
                .containsExactly(calendar.getStartDate(), calendar.getEndDate(), calendar.getScheduleDetails(), calendar.getScheduleType());
    }

    @Test
    @DisplayName("존재하는 일정을 삭제할 수 있다.")
    void whenDeleteExistingCalendar_thenShouldDeleteSuccessfully() {
        // Given
        Long calendarId = 1L;
        given(calendarCommandRepository.existsById(calendarId)).willReturn(true);

        // When
        calendarCommandService.deleteCalendarById(calendarId);

        // Then
        verify(calendarCommandRepository).deleteById(calendarId);
    }

    @Test
    @DisplayName("존재하지 않는 일정을 삭제하려고 하면 예외가 발생한다.")
    void whenDeleteNonExistingCalendar_thenShouldThrowException() {
        // Given
        Long calendarId = 1L;
        given(calendarCommandRepository.existsById(calendarId)).willReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> calendarCommandService.deleteCalendarById(calendarId));
        verify(calendarCommandRepository, never()).deleteById(anyLong());
    }
}