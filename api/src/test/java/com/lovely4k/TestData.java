package com.lovely4k;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;

public class TestData {

    public static QuestionForm questionForm(Long memberId) {
        return new QuestionForm(memberId,
                "test",
                QuestionChoices.builder()
                        .firstChoice("test1")
                        .secondChoice("test2")
                        .build());
    }

    public static Question question(QuestionForm questionForm, Long coupleId, String answer1, String answer2) {
        return Question.builder()
                .questionDay(1L)
                .questionForm(questionForm)
                .coupleId(coupleId)
                .boyAnswer(answer1)
                .girlAnswer(answer1)
                .build();
    }
}