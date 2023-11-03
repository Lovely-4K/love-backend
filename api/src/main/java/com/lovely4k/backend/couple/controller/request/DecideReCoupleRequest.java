package com.lovely4k.backend.couple.controller.request;

import com.lovely4k.backend.common.enumvalidator.EnumValue;
import com.lovely4k.backend.couple.Decision;
import com.lovely4k.backend.couple.service.request.DecideReCoupleServiceRequest;
import jakarta.validation.constraints.NotNull;

public record DecideReCoupleRequest(
    @NotNull
    @EnumValue(enumClass = Decision.class, message = "결정은 yes, no 만 가능합니다.", ignoreCase = true)
    String decision
) {
        public DecideReCoupleServiceRequest toServiceRequest() {
                return new DecideReCoupleServiceRequest(Decision.valueOf(decision.toUpperCase()));
        }
}
