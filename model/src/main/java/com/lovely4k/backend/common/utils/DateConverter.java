package com.lovely4k.backend.common.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateConverter {

    private DateConverter() {}

    public static long getDurationOfAppUsage(LocalDate startDate) {
        LocalDate date = startDate;
        LocalDate today = LocalDate.now();

        return ChronoUnit.DAYS.between(date, today);
    }
}