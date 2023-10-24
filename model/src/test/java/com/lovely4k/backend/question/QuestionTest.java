package com.lovely4k.backend.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionTest {

    @DisplayName("답변 유효성 검사 - 둘 다 비어있는 경우")
    @Test
    void validateAnswer_BothEmpty() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L);
        Question question = Question.create(1L, questionForm, 1L);
        question.updateBoyAnswer("");
        question.updateGirlAnswer("");

        assertThatThrownBy(question::validateAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("질문에 답변을 아직 안했습니다.");
    }

    @DisplayName("답변 유효성 검사 - 하나만 비어있는 경우")
    @Test
    void validateAnswer_OneEmpty() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L);

        Question question1 = Question.create(1L, questionForm, 1L);
        question1.updateBoyAnswer("answer");
        question1.updateGirlAnswer("");

        assertThatThrownBy(question1::validateAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("질문에 답변을 아직 안했습니다.");

        Question question2 = Question.create(1L, questionForm, 1L);
        question2.updateBoyAnswer("");
        question2.updateGirlAnswer("answer");

        assertThatThrownBy(question2::validateAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("질문에 답변을 아직 안했습니다.");
    }

    @DisplayName("답변 유효성 검사 - 둘 다 채워져 있는 경우")
    @Test
    void validateAnswer_BothFilled() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L);
        Question question = Question.create(1L, questionForm, 1L);
        question.updateBoyAnswer("boy answer");
        question.updateGirlAnswer("girl answer");

        assertThatCode(question::validateAnswer).doesNotThrowAnyException();
    }
}