package com.lovely4k.backend.question.service.request;

import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;

public record CreateServerQuestionFormServiceRequest(
    String questionContent,
    String firstChoice,
    String secondChoice,
    String thirdChoice,
    String fourthChoice,
    Long questionDay
) {
    public QuestionForm toEntity() {
        QuestionChoices questionChoices = QuestionChoices.create(firstChoice, secondChoice, thirdChoice, fourthChoice);

        return QuestionForm.create(null, questionContent, questionChoices, questionDay, QuestionFormType.SERVER);
    }
}