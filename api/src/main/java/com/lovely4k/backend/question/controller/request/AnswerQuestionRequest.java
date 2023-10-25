package com.lovely4k.backend.question.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record AnswerQuestionRequest(
        @Min(1) @Max(4)
        int choiceNumber
) {
}