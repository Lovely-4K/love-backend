package com.lovely4k.backend.calendar.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;

import java.time.LocalDate;

public record CreateCalendarResponse(
        long id,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        ScheduleType scheduleType
) {
    public static CreateCalendarResponse from() {
        return new CreateCalendarResponse(1L, LocalDate.now(), LocalDate.now(), "details", ScheduleType.DATE);
    }
}