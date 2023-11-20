package com.lovely4k.backend.question.controller.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AnsweredQuestionParamRequest {
    @Min(0)
    private Long id;

    @Min(1)
    private int limit = 10;
}