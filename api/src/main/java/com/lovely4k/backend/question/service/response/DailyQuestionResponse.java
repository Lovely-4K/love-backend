package com.lovely4k.backend.question.service.response;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;

public record DailyQuestionResponse(
        long questionId,
        String questionContent,
        String firstChoice,
        String secondChoice,
        String thirdChoice,
        String fourthChoice
) {

    public static DailyQuestionResponse from(Question question) {
        QuestionForm questionForm = question.getQuestionForm();
        QuestionChoices questionChoices = questionForm.getQuestionChoices();

        return new DailyQuestionResponse(
                question.getId(),
                questionForm.getQuestionContent(),
                questionChoices.getFirstChoice(),
                questionChoices.getSecondChoice(),
                questionChoices.getThirdChoice(),
                questionChoices.getFourthChoice()
        );
    }
}