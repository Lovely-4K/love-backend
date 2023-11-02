package com.lovely4k.backend.calendar.service.response;

import com.lovely4k.backend.calendar.repository.response.ColorResponse;
import com.lovely4k.backend.calendar.repository.response.FindAllCalendarsWithDateResponse;

import java.util.List;

public record FindAllCalendarsWithDateServiceResponse(
        ColorResponse firstColor,
        ColorResponse secondColor,
        List<ScheduleServiceResponse> schedules
) {
    public static FindAllCalendarsWithDateServiceResponse from(FindAllCalendarsWithDateResponse response) {
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

        return new FindAllCalendarsWithDateServiceResponse(
                response.firstColor(),
                response.secondColor(),
                scheduleServiceResponses
        );
    }
}