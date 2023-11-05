package com.lovely4k.backend.question.repository.response;

public record QuestionDetailsResponse(
    String questionContent,
    String myAnswer,
    String opponentAnswer,
    int myChoiceIndex,
    int opponentChoiceIndex,
    String myProfile,
    String opponentProfile
) {
}