package com.lovely4k.backend.question.controller.request;

import java.util.List;

public record CreateQuestionFormRequest(
        String questionContent,
        List<QuestionChoiceRequest> choices
) {
    public record QuestionChoiceRequest(String choice) {}
}