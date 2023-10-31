package com.lovely4k.backend.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @DisplayName("validateRequest를 통해 input을 검증할 수 있다.")
    @ValueSource(strings = {"CAFE", "FOOD", "ACCOMODATION", "CULTURE", "ETC", "cafe", "food", "accomodation", "culture", "etc"})
    @ParameterizedTest
    void validateRequest(String category) {
        // when && then
        Category.validateRequest(category);
    }

    @DisplayName("validateRequest를 통해 input을 검증할 수 있다.")
    @Test
    void validateRequestInvalidText() {
        // given
        String category = "LEISURE";

        // when && then
        assertThatThrownBy(
                () -> Category.validateRequest(category)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid input : " + category);
    }
}
