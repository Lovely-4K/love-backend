package com.lovely4k.backend.calendar.repository;

import java.time.LocalDate;

public record FindCalendarsWithDateRepositoryRequest(
    LocalDate from,
    LocalDate to

) {
}