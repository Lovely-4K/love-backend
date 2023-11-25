package com.lovely4k.backend.question.controller.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AnsweredQuestionParamRequest {
    @Min(value = 0, message = "id는 0 이상이어야 합니다.")
    private Long id;

    @Min(value = 1, message = "1개 이상의 limit을 입력하세요.")
    private int limit = 10;
}