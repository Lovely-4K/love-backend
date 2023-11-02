package com.lovely4k.backend.calendar.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;

import java.time.LocalDate;

public record ScheduleServiceResponse(

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        ScheduleType scheduleType
) {
}