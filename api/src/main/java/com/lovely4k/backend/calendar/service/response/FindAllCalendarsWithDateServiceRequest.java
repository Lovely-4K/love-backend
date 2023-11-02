package com.lovely4k.backend.calendar.service.response;

import java.time.LocalDate;

public record FindAllCalendarsWithDateServiceRequest(
        LocalDate startDate,
        LocalDate endDate,
        long coupleId
) {
}