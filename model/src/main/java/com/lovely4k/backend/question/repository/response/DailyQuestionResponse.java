package com.lovely4k.backend.question.repository.response;

import com.lovely4k.backend.question.QuestionFormType;

public record DailyQuestionResponse(
    long questionId,
    String questionContent,
    String firstChoice,
    String secondChoice,
    String thirdChoice,
    String fourthChoice,
    QuestionFormType questionFormType
) {

}