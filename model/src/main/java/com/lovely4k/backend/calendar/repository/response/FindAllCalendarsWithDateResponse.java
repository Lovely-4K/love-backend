package com.lovely4k.backend.calendar.repository.response;

import com.lovely4k.backend.calendar.ScheduleType;

import java.time.LocalDate;

public record FindAllCalendarsWithDateResponse(
    long boyId,
    String boyCalendarColor,
    long girlId,
    String girlCalendarColor,
    LocalDate startDate,
    LocalDate endDate,
    String scheduleDetails,
    ScheduleType scheduleType
) {
}