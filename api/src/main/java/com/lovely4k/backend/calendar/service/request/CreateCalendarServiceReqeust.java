package com.lovely4k.backend.calendar.service.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.Calendar;

import java.time.LocalDate;

public record CreateCalendarServiceReqeust(

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        String scheduleDetails,

        String scheduleType
) {
    public Calendar toEntity(long ownerId, long memberId) {
        return Calendar.create(startDate, endDate, ownerId, scheduleType, scheduleDetails, memberId);
    }

}