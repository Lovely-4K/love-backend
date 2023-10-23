package com.lovely4k.backend.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateConverter {

    public static long getDurationOfAppUsage(LocalDateTime startDate) {
        LocalDate date = startDate.toLocalDate();
        LocalDate today = LocalDate.now();

        return ChronoUnit.DAYS.between(date, today);
    }
}