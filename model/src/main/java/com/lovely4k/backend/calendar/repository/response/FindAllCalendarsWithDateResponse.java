package com.lovely4k.backend.calendar.repository.response;

import java.util.List;

public record FindAllCalendarsWithDateResponse(
        ColorResponse firstColor,
        ColorResponse secondColor,
        List<ScheduleResponse> schedules

) {
}