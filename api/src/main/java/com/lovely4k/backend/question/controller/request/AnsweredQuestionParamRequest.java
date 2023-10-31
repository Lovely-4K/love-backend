package com.lovely4k.backend.question.controller.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AnsweredQuestionParamRequest {
    @Min(0)
    private Long id = 0L;

    @Min(1)
    private final Long coupleId;

    @Min(1)
    private int limit = 10;
}