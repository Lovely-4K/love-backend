package com.lovely4k.backend.question.service.response;

import java.util.List;

public record CreateQuestionResponse(
        long questionId,
        String questionContent,
        List<QuestionChoiceResponse> questionChoices
) {
    public record QuestionChoiceResponse(String choice) {}
}