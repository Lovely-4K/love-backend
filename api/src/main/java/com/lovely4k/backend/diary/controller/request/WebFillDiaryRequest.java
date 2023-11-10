package com.lovely4k.backend.diary.controller.request;

import com.lovely4k.backend.diary.service.request.FillDiaryRequest;
import jakarta.validation.constraints.NotBlank;

public record WebFillDiaryRequest(
    @NotBlank(message = "text of diary must not be null and empty")
    String text
) {
    public FillDiaryRequest toServiceRequest() {
        return new FillDiaryRequest(text);
    }
}
