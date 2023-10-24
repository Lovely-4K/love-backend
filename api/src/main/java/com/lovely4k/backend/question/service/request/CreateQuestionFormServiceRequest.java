package com.lovely4k.backend.question.service.request;

import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;

public record CreateQuestionFormServiceRequest(
        String questionContent,
        String firstChoice,
        String secondChoice,
        String thirdChoice,
        String fourthChoice
) {
    public QuestionForm toEntity(Long memberId) {
        QuestionChoices questionChoices = QuestionChoices.builder()
                .firstChoice(this.firstChoice)
                .secondChoice(this.secondChoice)
                .thirdChoice(this.thirdChoice)
                .fourthChoice(this.fourthChoice)
                .build();

        return new QuestionForm(memberId, questionContent, questionChoices);
    }
}