package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Photos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PhotoListResponseTest {

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
        PhotoListResponse photoListResponse = PhotoListResponse.from(photos);

        // then
        assertAll(
                () -> assertThat(photoListResponse.getFifthImage()).isNull(),
                () -> assertThat(photoListResponse.getFourthImage()).isNull(),
                () -> assertThat(photoListResponse.getFirstImage()).isNotNull().isEqualTo("image-url1"),
                () -> assertThat(photoListResponse.getSecondImage()).isNotNull().isEqualTo("image-url2"),
                () -> assertThat(photoListResponse.getThirdImage()).isNotNull().isEqualTo("image-url3")
        );

    }

}
