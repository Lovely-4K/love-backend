package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Photos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PhotoListTest {

    @DisplayName("PhotoList의 from 메서드를 통해 PhotoList를 생성할 수 있다. ")
    @Test
    void from() {
        // given
        Photos photos = Photos.builder()
                .firstImage("image-url1")
                .secondImage("image-url2")
                .thirdImage("image-url3")
                .build();

        // when
        PhotoList photoList = PhotoList.from(photos);

        // then
        assertAll(
                () -> assertThat(photoList.getFifthImage()).isNull(),
                () -> assertThat(photoList.getFourthImage()).isNull(),
                () -> assertThat(photoList.getFirstImage()).isNotNull().isEqualTo("image-url1"),
                () -> assertThat(photoList.getSecondImage()).isNotNull().isEqualTo("image-url2"),
                () -> assertThat(photoList.getThirdImage()).isNotNull().isEqualTo("image-url3")
        );

    }

}
