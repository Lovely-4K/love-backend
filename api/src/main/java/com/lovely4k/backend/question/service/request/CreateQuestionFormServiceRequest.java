package com.lovely4k.backend.question.service.request;

import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;

public record CreateQuestionFormServiceRequest(
    String questionContent,
    String firstChoice,
    String secondChoice,
    String thirdChoice,
    String fourthChoice
) {
    public QuestionForm toEntity(Long memberId, Long questionDay) {
        QuestionChoices questionChoices = QuestionChoices.create(firstChoice, secondChoice, thirdChoice, fourthChoice);

        return QuestionForm.create(memberId, questionContent, questionChoices, questionDay, QuestionFormType.CUSTOM);
    }
}