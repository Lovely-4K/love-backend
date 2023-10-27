package com.lovely4k.backend.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class QuestionChoicesTest {

    @DisplayName("QuestionChoices 객체 생성 - 첫 번째, 두 번째 선택지가 비어있는 경우")
    @Test
    void create_WithEmptyFirstAndSecondChoices() {
        assertAll(
                () -> assertThatThrownBy(() -> QuestionChoices.create(null, null, "choice3", "choice4"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("첫 번째 선택지, 두 번째 선택지는 무조건 존재해야 합니다."),
                () -> assertThatThrownBy(() -> QuestionChoices.create("", "", "choice3", "choice4"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("첫 번째 선택지, 두 번째 선택지는 무조건 존재해야 합니다."),
                () -> assertThatThrownBy(() -> QuestionChoices.create("choice1", null, "choice3", "choice4"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("첫 번째 선택지, 두 번째 선택지는 무조건 존재해야 합니다."),
                () -> assertThatThrownBy(() -> QuestionChoices.create(null, "choice2", "choice3", "choice4"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("첫 번째 선택지, 두 번째 선택지는 무조건 존재해야 합니다.")
        );
    }
}