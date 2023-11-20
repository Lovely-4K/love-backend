package com.lovely4k.backend.question.repository.response;

public record QuestionGameResponse(
    String questionContent,
    String firstChoice,
    String secondChoice,
    String thirdChoice,
    String fourthChoice,
    int opponentChoiceIndex
) {
}
