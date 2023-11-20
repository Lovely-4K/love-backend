package com.lovely4k.backend.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LocationTest {

    @DisplayName("Location create method 테스트")
    @Test
    void create() {
        // given
        Long kakaoMapId = 1L;
        String address = "경기도 고양시";
        String placeName = "starbucks";
        String category = "ACCOMODATION";

        // when
        Location location = Location.create(kakaoMapId, address, placeName, BigDecimal.ZERO, BigDecimal.ONE, category);
        // then
        assertAll(
            () -> assertThat(location.getKakaoMapId()).isEqualTo(1L),
            () -> assertThat(location.getAddress()).isEqualTo("경기도 고양시"),
            () -> assertThat(location.getPlaceName()).isEqualTo("starbucks"),
            () -> assertThat(location.getCategory()).isEqualTo(Category.ACCOMODATION),
            () -> assertThat(location.getLatitude()).isEqualTo(BigDecimal.ZERO),
            () -> assertThat(location.getLongitude()).isEqualTo(BigDecimal.ONE)
        );

    }

    @DisplayName("update를 통해서 Location의 카테고리를 업데이트 할 수 있다.")
    @Test
    void update() {
        // given
        Location location = Location.create(1L, "경기도 고양시", "카페베네", BigDecimal.ZERO, BigDecimal.ZERO, Category.ACCOMODATION);

        // when
        location.update("food");

        // then
        assertThat(location.getCategory()).isEqualTo(Category.FOOD);
    }
}
