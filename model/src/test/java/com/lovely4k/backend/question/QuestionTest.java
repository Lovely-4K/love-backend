package com.lovely4k.backend.question;

import com.lovely4k.backend.member.Sex;
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
        question1.updateAnswer(1, Sex.MALE);

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
        question.updateAnswer(1, Sex.MALE);
        question.updateAnswer(2, Sex.FEMALE);

        assertThatCode(question::validateAnswer).doesNotThrowAnyException();
    }

    @DisplayName("답변 업데이트 - 성별에 따른 답변 업데이트")
    @Test
    void updateAnswer_BySex() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        question.updateAnswer(1, Sex.MALE);
        question.updateAnswer(2, Sex.FEMALE);

        assertThat(question.getBoyChoiceAnswer()).isEqualTo("choice1");
        assertThat(question.getGirlChoiceAnswer()).isEqualTo("choice2");
    }

    @DisplayName("답변 업데이트 - 유효하지 않은 선택")
    @Test
    void updateAnswer_InvalidChoice() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(() -> question.updateAnswer(5, Sex.MALE))
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

    @DisplayName("답변 가져오기 - 유효한 인덱스")
    @Test
    void getChoiceAnswerByIndex_ValidIndex() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", "choice3", "choice4");
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        question.updateAnswer(1, Sex.MALE);
        question.updateAnswer(2, Sex.FEMALE);

        assertThat(question.getBoyChoiceAnswer()).isEqualTo("choice1");
        assertThat(question.getGirlChoiceAnswer()).isEqualTo("choice2");
    }

    @DisplayName("답변 가져오기 - 아직 답변하지 않은 경우")
    @Test
    void getChoiceAnswerByIndex_InvalidIndex() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", "choice3", "choice4");
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(question::getBoyChoiceAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("아직 답변을 하지 않았습니다.");

        assertThatThrownBy(question::getGirlChoiceAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("아직 답변을 하지 않았습니다.");
    }

    @DisplayName("답변 업데이트 - 유효한 선택 (case 3, 4)")
    @Test
    void updateAnswer_ValidChoice_Case3_4() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", "choice3", "choice4");
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        question.updateAnswer(3, Sex.MALE);
        question.updateAnswer(4, Sex.FEMALE);

        assertThat(question.getBoyChoiceAnswer()).isEqualTo("choice3");
        assertThat(question.getGirlChoiceAnswer()).isEqualTo("choice4");
    }

    @DisplayName("답변 업데이트 - 유효하지 않은 선택 (empty choice)")
    @Test
    void updateAnswer_InvalidChoice_EmptyChoice() {
        QuestionChoices questionChoices = QuestionChoices.create("choice1", "choice2", null, null);
        QuestionForm questionForm = QuestionForm.create(1L, "questionContent", questionChoices, 1L, QuestionFormType.SERVER);
        Question question = Question.create(1L, questionForm, 1L);

        assertThatThrownBy(() -> question.updateAnswer(3, Sex.MALE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 선택입니다.: 3");

        assertThatThrownBy(() -> question.updateAnswer(4, Sex.FEMALE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 선택입니다.: 4");
    }

}