package com.lovely4k.backend.calendar.service.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record UpdateCalendarServiceRequest(

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,
        String scheduleType
) {
}