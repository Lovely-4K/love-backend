package com.lovely4k.backend.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LocationTest {

    @DisplayName("Location create method 테스트")
    @Test
    void create() {
        // given
        Long kakaoMapId = 1L;
        String address = "경기도 고양시";
        String category = "ACCOMODATION";

        // when
        Location location = Location.create(kakaoMapId, address, category);

        // then
        assertAll(
                () -> assertThat(location.getKakaoMapId()).isEqualTo(1L),
                () -> assertThat(location.getAddress()).isEqualTo("경기도 고양시"),
                () -> assertThat(location.getCategory()).isEqualTo(Category.ACCOMODATION)
        );

    }
}