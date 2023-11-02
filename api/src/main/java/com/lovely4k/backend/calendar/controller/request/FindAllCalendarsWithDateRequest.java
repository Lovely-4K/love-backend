package com.lovely4k.backend.calendar.controller.request;

import com.lovely4k.backend.calendar.service.response.FindAllCalendarsWithDateServiceRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record FindAllCalendarsWithDateRequest(

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        long coupleId
) {
    public FindAllCalendarsWithDateServiceRequest toServiceDto() {
        return new FindAllCalendarsWithDateServiceRequest(startDate, endDate, coupleId);
    }
}