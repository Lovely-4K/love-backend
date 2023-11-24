package com.lovely4k.backend.calendar.repository.response;

import com.lovely4k.backend.calendar.ScheduleType;

import java.time.LocalDate;

public record FindRecentCalendarsResponse(
    long calendarId,
    long myId,
    String myCalendarColor,
    long opponentId,
    String opponentCalendarColor,
    LocalDate startDate,
    LocalDate endDate,
    String scheduleDetails,
    ScheduleType scheduleType,
    long ownerId
) {
}