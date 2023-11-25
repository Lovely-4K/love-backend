package com.lovely4k.backend.calendar.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.response.FindCalendarsWithDateResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record FindCalendarsWithDateServiceResponse(
    ColorResponse colorInfo,
    List<ScheduleServiceResponse> schedules
) {
    private record ColorResponse(
        long myId,
        String myCalendarColor,
        long opponentId,
        String opponentCalendarColor
    ) {
        private static ColorResponse from(FindCalendarsWithDateResponse response) {
            return new ColorResponse(
                response.myId(),
                response.myCalendarColor(),
                response.opponentId(),
                response.opponentCalendarColor()
            );
        }
    }

    private record ScheduleServiceResponse(
        long calendarId,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        ScheduleType scheduleType,
        long ownerId
    ) {
        private static ScheduleServiceResponse from(FindCalendarsWithDateResponse response) {
            return new ScheduleServiceResponse(
                response.calendarId(),
                response.startDate(),
                response.endDate(),
                response.scheduleDetails(),
                response.scheduleType(),
                response.ownerId()
            );
        }
    }

    public static FindCalendarsWithDateServiceResponse from(List<FindCalendarsWithDateResponse> response) {
        if (response.isEmpty()) {
            return new FindCalendarsWithDateServiceResponse(null, Collections.emptyList());
        }

        List<ScheduleServiceResponse> scheduleServiceResponses = response
            .stream()
            .map(ScheduleServiceResponse::from)
            .toList();

        return new FindCalendarsWithDateServiceResponse(
            ColorResponse.from(response.get(0)),
            scheduleServiceResponses
        );
    }
}