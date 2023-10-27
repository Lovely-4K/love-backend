package com.lovely4k.backend.couple.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.couple.service.request.CoupleProfileEditServiceRequest;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CoupleProfileEditRequest(
    @NotNull(message = "만난날을 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate meetDay
) {
    public CoupleProfileEditServiceRequest toServiceRequest() {
        return new CoupleProfileEditServiceRequest(meetDay);
    }
}
