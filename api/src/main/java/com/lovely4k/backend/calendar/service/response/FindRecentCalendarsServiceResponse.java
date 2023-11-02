package com.lovely4k.backend.calendar.service.response;

import com.lovely4k.backend.calendar.repository.response.ColorResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;

import java.util.List;

public record FindRecentCalendarsServiceResponse(
        ColorResponse firstColor,
        ColorResponse secondColor,
        List<ScheduleServiceResponse> schedules
) {
    public static FindRecentCalendarsServiceResponse from(FindRecentCalendarsResponse response) {
        List<ScheduleServiceResponse> scheduleServiceResponses = response
                .schedules()
                .stream()
                .map(schedule -> new ScheduleServiceResponse(
                        schedule.startDate(),
                        schedule.endDate(),
                        schedule.scheduleDetails(),
                        schedule.scheduleType()
                ))
                .toList();

        return new FindRecentCalendarsServiceResponse(
                response.firstColor(),
                response.secondColor(),
                scheduleServiceResponses
        );
    }
}