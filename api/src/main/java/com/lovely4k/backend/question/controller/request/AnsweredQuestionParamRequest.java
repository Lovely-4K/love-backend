package com.lovely4k.backend.question.controller.request;

import lombok.Data;

@Data
public class AnsweredQuestionParamRequest {
    private Long id = 0L;
    private final Long coupleId;
    private int limit = 10;
}