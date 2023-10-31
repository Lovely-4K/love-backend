package com.lovely4k;

import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;

public class TestData {

    public static QuestionForm questionForm(Long memberId) {
        QuestionChoices choices = QuestionChoices.create("test1", "test2", null, null);
        return QuestionForm.create(memberId, "test", choices, 1L);
    }

    public static Question question(QuestionForm questionForm, Long coupleId, String answer1, String answer2) {
        return Question.create(coupleId, questionForm, 1L);
    }
}