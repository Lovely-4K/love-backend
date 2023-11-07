package com.lovely4k.backend.calendar.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.service.request.UpdateCalendarServiceRequest;

import java.time.LocalDate;

public record UpdateCalendarRequest(

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        ScheduleType scheduleType
) {
    public UpdateCalendarServiceRequest toServiceDto() {
        return new UpdateCalendarServiceRequest(startDate, endDate, scheduleDetails, scheduleType);
    }
}