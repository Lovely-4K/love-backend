package com.lovely4k.backend.calendar.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.Calendar;
import com.lovely4k.backend.calendar.ScheduleType;

import java.time.LocalDate;

public record CreateCalendarResponse(
    long id,

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate,

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate,

    String scheduleDetails,
    ScheduleType scheduleType,
    long ownerId
) {
    public static CreateCalendarResponse from(Calendar calendar) {
        return new CreateCalendarResponse(
            calendar.getId(),
            calendar.getStartDate(),
            calendar.getEndDate(),
            calendar.getScheduleDetails(),
            calendar.getScheduleType(),
            calendar.getOwnerId()
        );
    }
}