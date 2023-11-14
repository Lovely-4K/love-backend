package com.lovely4k;

import com.lovely4k.backend.calendar.Calendar;
import com.lovely4k.backend.question.Question;
import com.lovely4k.backend.question.QuestionChoices;
import com.lovely4k.backend.question.QuestionForm;
import com.lovely4k.backend.question.QuestionFormType;

import java.time.LocalDate;

import java.time.LocalDate;

public class TestData {

    public static QuestionForm questionForm(Long memberId) {
        QuestionChoices choices = QuestionChoices.create("test1", "test2", null, null);
        return QuestionForm.create(memberId, "test", choices, 1L, QuestionFormType.SERVER);
    }

    public static Question question(QuestionForm questionForm, Long coupleId, String answer1, String answer2) {
        return Question.create(coupleId, questionForm, 1L);
    }

    public static Calendar calendar(long ownerId, long coupleId) {
        return Calendar.create(LocalDate.now(), LocalDate.now(), ownerId, "DATE", "details",coupleId);
    }
}