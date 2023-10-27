package com.lovely4k.backend.question.service.response;

import com.lovely4k.backend.question.Question;

public record QuestionDetailsResponse(
        String questionContent,
        String boyAnswer,
        String girlAnswer
) {

    public static QuestionDetailsResponse from(Question question) {
        return new QuestionDetailsResponse(question.getQuestionForm().getQuestionContent(), question.getBoyChoiceAnswer(), question.getGirlChoiceAnswer());
    }
}