package com.lovely4k.backend.calendar.controller.request;

import com.lovely4k.backend.calendar.repository.FindCalendarsWithDateRepositoryRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record FindCalendarsWithDateRequest(

    @NotNull(message = "from에 해당하는 날짜를 입력해주세요: yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate from,

    @NotNull(message = "to에 해당하는 날짜를 입력해주세요: yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate to,

    long coupleId
) {
    public FindCalendarsWithDateRepositoryRequest toRepositoryDto() {
        return new FindCalendarsWithDateRepositoryRequest(to, from, coupleId);
    }
}