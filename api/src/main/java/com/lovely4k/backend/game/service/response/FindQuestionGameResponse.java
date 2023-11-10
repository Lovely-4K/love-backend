package com.lovely4k.backend.game.service.response;

import com.lovely4k.backend.question.Question;

public record FindQuestionGameResponse(
    Long questionId,
    String questionContent,
    String firstChoice,
    String secondChoice,
    String thirdChoice,
    String fourthChoice
) {
    public static FindQuestionGameResponse from(Question question) {
        return new FindQuestionGameResponse(question.getId(),
            question.getQuestionForm().getQuestionContent(),
            question.getQuestionForm().getQuestionChoices().getFirstChoice(),
            question.getQuestionForm().getQuestionChoices().getSecondChoice(),
            question.getQuestionForm().getQuestionChoices().getThirdChoice(),
            question.getQuestionForm().getQuestionChoices().getFourthChoice());
    }
}
