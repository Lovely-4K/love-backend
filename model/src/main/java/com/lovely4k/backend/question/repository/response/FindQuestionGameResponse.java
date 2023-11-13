package com.lovely4k.backend.question.repository.response;

public record FindQuestionGameResponse(
    String questionContent,
    String firstChoice,
    String secondChoice,
    String thirdChoice,
    String fourthChoice,
    int boyChoiceIndex,
    int girlChoiceIndex
) {
}
