package com.lovely4k.backend.question.repository.response;

import java.util.List;

public record AnsweredQuestionResponse(
    List<QuestionResponse> answeredQuestions
) {
}