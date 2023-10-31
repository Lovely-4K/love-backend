package com.lovely4k.backend.diary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PhotosTest {

    @DisplayName("create 메서드를 통해 Photos를 생성할 수 있다. ")
    @Test
    void create() {
        // when
        Photos photos = Photos.create(List.of("image-url1", "image-url2", "image-url3", "image-url4", "image-url5"));

        // then
        assertAll(
                () -> assertThat(photos.getFirstImage()).isEqualTo("image-url1"),
                () -> assertThat(photos.getSecondImage()).isEqualTo("image-url2"),
                () -> assertThat(photos.getThirdImage()).isEqualTo("image-url3"),
                () -> assertThat(photos.getFourthImage()).isEqualTo("image-url4"),
                () -> assertThat(photos.getFifthImage()).isEqualTo("image-url5")
        );
    }

    @DisplayName("create시에 만약 이미지가 없는 경우 imageUrl이 할당되지 않은 Photos가 반환된다.")
    @Test
    void createNoImages() {
        // when
        Photos photos = Photos.create(Collections.emptyList());

        // then
        assertAll(
                () -> assertThat(photos.getFirstImage()).isNull(),
                () -> assertThat(photos.getSecondImage()).isNull(),
                () -> assertThat(photos.getThirdImage()).isNull(),
                () -> assertThat(photos.getFourthImage()).isNull(),
                () -> assertThat(photos.getFifthImage()).isNull()
        );
    }

    private static Stream<Arguments> provideImages() {
        return Stream.of(
                Arguments.of(List.of("image-url"), 1),
                Arguments.of(List.of("image-url", "image-url"), 2),
                Arguments.of(List.of("image-url", "image-url", "image-url"), 3),
                Arguments.of(List.of("image-url", "image-url", "image-url", "image-url"), 4),
                Arguments.of(List.of("image-url", "image-url", "image-url", "image-url", "image-url"), 5)
        );
    }

    @DisplayName("countOfImages 메서드를 통해 Photos에 담긴 이미지의 개수를 조회할 수 있다.")
    @MethodSource("provideImages")
    @ParameterizedTest
    void countOfImages(List imageList, int size) {
        Photos photos = Photos.create(imageList);

        // when
        int countOfImages = photos.countOfImages();

        // then
        assertThat(countOfImages).isEqualTo(size);
    }
}
