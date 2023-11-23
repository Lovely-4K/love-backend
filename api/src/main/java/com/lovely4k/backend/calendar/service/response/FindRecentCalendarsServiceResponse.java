package com.lovely4k.backend.calendar.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record FindRecentCalendarsServiceResponse(
    ColorResponse colorInfo,
    List<ScheduleServiceResponse> schedules
) {
    private record ColorResponse(
        long boyId,
        String boyCalendarColor,
        long girlId,
        String girlCalendarColor
    ) {
        private static ColorResponse from(FindRecentCalendarsResponse response) {
            return new ColorResponse(
                response.boyId(),
                response.boyCalendarColor(),
                response.girlId(),
                response.girlCalendarColor()
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
        private static ScheduleServiceResponse from(FindRecentCalendarsResponse response) {
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

    public static FindRecentCalendarsServiceResponse from(List<FindRecentCalendarsResponse> responses) {
        if (responses.isEmpty()) {
            return new FindRecentCalendarsServiceResponse(null, Collections.emptyList());
        }

        List<ScheduleServiceResponse> scheduleServiceResponses = responses
            .stream()
            .map(ScheduleServiceResponse::from)
            .toList();

        return new FindRecentCalendarsServiceResponse(
            ColorResponse.from(responses.get(0)),
            scheduleServiceResponses
        );
    }
}