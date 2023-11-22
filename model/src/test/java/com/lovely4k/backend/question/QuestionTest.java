package com.lovely4k.backend.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class QuestionTest {

    @DisplayName("답변 유효성 검사 - 둘 다 비어있는 경우")
    @Test
    void validateAnswer_BothEmpty() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(question::validateAnswer)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("질문에 답변을 아직 안했습니다.");
    }

    @DisplayName("답변 유효성 검사 - 하나만 비어있는 경우")
    @Test
    void validateAnswer_OneEmpty() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);

        Question question1 = Question.create(1L, questionForm, 1L);
        question1.updateAnswer(1, 1L, 1L);

        assertThatThrownBy(question1::validateAnswer)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("질문에 답변을 아직 안했습니다.");

        Question question2 = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(question2::validateAnswer)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("질문에 답변을 아직 안했습니다.");
    }

    @DisplayName("답변 유효성 검사 - 둘 다 채워져 있는 경우")
    @Test
    void validateAnswer_BothFilled() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);
        question.updateAnswer(1, 1L, 1L);
        question.updateAnswer(2, 1L, 2L);

        assertThatCode(question::validateAnswer).doesNotThrowAnyException();
    }

    @DisplayName("답변 업데이트 - 유효하지 않은 선택")
    @Test
    void updateAnswer_InvalidChoice() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(() -> question.updateAnswer(5, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 선택입니다.");
    }

    @DisplayName("Question 객체 생성 - 필수 필드가 null인 경우")
    @Test
    void create_WithNullFields() {
        List<Runnable> tests = Arrays.asList(
                () -> Question.create(null, new QuestionForm(), 1L),
                () -> Question.create(1L, null, 1L),
                () -> Question.create(1L, new QuestionForm(), null)
        );

        List<String> expectedMessages = Arrays.asList(
                "coupleId는 null값이 될 수 없습니다.",
                "questionForm은 null 값이 될 수 없습니다.",
                "questionDay는 null 값이 될 수 없습니다."
        );

        for (int i = 0; i < tests.size(); i++) {
            try {
                tests.get(i).run();
            } catch (NullPointerException e) {
                assertThat(e).hasMessage(expectedMessages.get(i));
            }
        }
    }


    @DisplayName("답변 업데이트 - 유효하지 않은 선택 (empty choice)")
    @Test
    void updateAnswer_InvalidChoice_EmptyChoice() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(() -> question.updateAnswer(3, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 선택입니다.: 3");

        assertThatThrownBy(() -> question.updateAnswer(4, 1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 선택입니다.: 4");
    }

}