package com.lovely4k.backend.question.service.response;

import com.lovely4k.backend.question.Question;

import java.util.List;

public record AnsweredQuestionResponse(
        List<QuestionResponse> answeredQuestions
) {
    public static AnsweredQuestionResponse from(List<Question> questions) {
        return new AnsweredQuestionResponse(questions.stream().map(QuestionResponse::from).toList());
    }
    public record QuestionResponse(
            long questionId,
            String questionContent
    ) {
        public static QuestionResponse from(Question question) {
            return new QuestionResponse(question.getId(), question.getQuestionForm().getQuestionContent());
        }
    }
}