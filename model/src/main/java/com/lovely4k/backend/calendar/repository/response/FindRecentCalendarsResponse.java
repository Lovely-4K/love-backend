package com.lovely4k.backend.calendar.repository.response;

import java.util.List;

public record FindRecentCalendarsResponse(
        ColorResponse firstColor,
        ColorResponse secondColor,
        List<ScheduleResponse> schedules
) {
}