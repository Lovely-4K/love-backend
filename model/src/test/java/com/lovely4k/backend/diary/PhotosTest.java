package com.lovely4k.backend.diary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PhotosTest {

    @DisplayName("create 메서드를 통해 Photos를 생성할 수 있다. ")
    @Test
    void create() {
        // when
        Photos photos = Photos.create(List.of("image-url1", "image-url2", "image-url3", "image-url4"));

        // then
        assertAll(
                () -> assertThat(photos.getFirstImage()).isEqualTo("image-url1"),
                () -> assertThat(photos.getSecondImage()).isEqualTo("image-url2"),
                () -> assertThat(photos.getThirdImage()).isEqualTo("image-url3"),
                () -> assertThat(photos.getFourthImage()).isEqualTo("image-url4")
        );
    }

    @DisplayName("countOfImages 메서드를 통해 Photos에 담긴 이미지의 개수를 조회할 수 있다.")
    @Test
    void countOfImages() {
        // given
        Photos photos = Photos.create(List.of("image-url1", "image-url2", "image-url3", "image-url4"));

        // when
        int countOfImages = photos.countOfImages();

        // then
        assertThat(countOfImages).isEqualTo(4);
    }
}
