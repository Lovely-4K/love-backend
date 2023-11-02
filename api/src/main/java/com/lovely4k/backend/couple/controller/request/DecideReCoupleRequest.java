package com.lovely4k.backend.couple.controller.request;

import jakarta.validation.constraints.NotNull;

public record DecideReCoupleRequest(
        @NotNull
        String decision
) {
}
