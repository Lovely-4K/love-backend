package com.lovely4k.backend.calendar.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.response.FindAllCalendarsWithDateResponse;

import java.time.LocalDate;
import java.util.List;

public record FindAllCalendarsWithDateServiceResponse(
    ColorResponse colorInfo,
    List<ScheduleServiceResponse> schedules
) {
    private record ColorResponse(
        long boyId,
        String boyCalendarColor,
        long girlId,
        String girlCalendarColor
    ) {
        private static ColorResponse from(FindAllCalendarsWithDateResponse response) {
            return new ColorResponse(
                response.boyId(),
                response.boyCalendarColor(),
                response.girlId(),
                response.girlCalendarColor()
            );
        }
    }

    private record ScheduleServiceResponse(

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        ScheduleType scheduleType
    ) {
        private static ScheduleServiceResponse from(FindAllCalendarsWithDateResponse response) {
            return new ScheduleServiceResponse(
                response.startDate(),
                response.endDate(),
                response.scheduleDetails(),
                response.scheduleType()
            );
        }
    }

    public static FindAllCalendarsWithDateServiceResponse from(List<FindAllCalendarsWithDateResponse> response) {
        List<ScheduleServiceResponse> scheduleServiceResponses = response
            .stream()
            .map(ScheduleServiceResponse::from)
            .toList();

        return new FindAllCalendarsWithDateServiceResponse(
            ColorResponse.from(response.get(0)),
            scheduleServiceResponses
        );
    }
}