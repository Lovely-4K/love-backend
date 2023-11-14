package com.lovely4k.backend.calendar.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.common.enumvalidator.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record CreateCalendarRequest(

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @NotBlank(message = "일정의 상세 정보를 입력해 주세요")
        @Length(min = 1, max = 255, message = "1 ~ 255글자 사이를 입력해주세요")
        String scheduleDetails,

        @EnumValue(enumClass = ScheduleType.class, message = "유효하지 않은 일정 타입입니다. TRAVEL, DATE, ANNIVERSARY, PERSONAL, ETC 중 입력하세요", ignoreCase = true)
        String scheduleType
) {
    public CreateCalendarServiceReqeust toServiceDto() {
        return new CreateCalendarServiceReqeust(startDate, endDate, scheduleDetails, scheduleType);
    }
}