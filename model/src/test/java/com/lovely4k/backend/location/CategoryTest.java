package com.lovely4k.backend.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {


    @DisplayName("validateRequest를 통해 input을 검증할 수 있다.")
    @Test
    void validateRequest() {
        // given
        String category = "LEISURE";

        // when && then
        assertThatThrownBy(
                () -> Category.validateRequest(category)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid input : " + category);
    }
}
