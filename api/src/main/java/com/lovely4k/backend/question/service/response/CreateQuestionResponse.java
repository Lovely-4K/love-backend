package com.lovely4k.backend.question.service.response;

public record CreateQuestionResponse(
        long questionId,
        String questionContent,
        String firstChoice,
        String secondChoice,
        String thirdChoice,
        String fourthChoice
) {
}