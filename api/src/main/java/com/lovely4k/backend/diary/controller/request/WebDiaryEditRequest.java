package com.lovely4k.backend.diary.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.common.enumvalidator.EnumValue;
import com.lovely4k.backend.diary.service.request.DiaryEditRequest;
import com.lovely4k.backend.location.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record WebDiaryEditRequest(

    @NotNull
    @Positive(message = "score must be positive")
    @Max(value = 5, message = "score cannot exceed 5")
    Integer score,
    @NotNull(message = "datingDay must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate datingDay,
    @NotNull
    @EnumValue(enumClass = Category.class, message = "invalid category", ignoreCase = true)
    String category,
    @NotBlank(message = "text of diary must not be null and empty")
    String myText,
    @NotBlank(message = "text of diary must not be null and empty")
    String opponentText,
    List<String> images
) {
    public DiaryEditRequest toServiceRequest() {
        return new DiaryEditRequest(score, datingDay, category, myText, opponentText, images);
    }
}
