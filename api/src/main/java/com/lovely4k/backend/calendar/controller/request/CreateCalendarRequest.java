package com.lovely4k.backend.calendar.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;

import java.time.LocalDate;

public record CreateCalendarRequest(

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        ScheduleType scheduleType
) {
    public CreateCalendarServiceReqeust toServiceDto() {
        return new CreateCalendarServiceReqeust(startDate, endDate, scheduleDetails, scheduleType);
    }
}