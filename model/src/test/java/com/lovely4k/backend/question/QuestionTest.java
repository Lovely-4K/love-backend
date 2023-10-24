package com.lovely4k.backend.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionTest {

    @DisplayName("답변 유효성 검사 - 둘 다 비어있는 경우")
    @Test
    void validateAnswer_BothEmpty() {
        Question question = Question.builder()
                .boyAnswer("")
                .girlAnswer("")
                .build();

        assertThatThrownBy(question::validateAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("질문에 답변을 아직 안했습니다.");
    }

    @DisplayName("답변 유효성 검사 - 하나만 비어있는 경우")
    @Test
    void validateAnswer_OneEmpty() {
        Question question1 = Question.builder()
                .boyAnswer("answer")
                .girlAnswer("")
                .build();

        assertThatThrownBy(question1::validateAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("질문에 답변을 아직 안했습니다.");

        Question question2 = Question.builder()
                .boyAnswer("")
                .girlAnswer("answer")
                .build();

        assertThatThrownBy(question2::validateAnswer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("질문에 답변을 아직 안했습니다.");
    }

    @DisplayName("답변 유효성 검사 - 둘 다 채워져 있는 경우")
    @Test
    void validateAnswer_BothFilled() {
        Question question = Question.builder()
                .boyAnswer("boy answer")
                .girlAnswer("girl answer")
                .build();

        assertThatCode(question::validateAnswer).doesNotThrowAnyException();
    }
}