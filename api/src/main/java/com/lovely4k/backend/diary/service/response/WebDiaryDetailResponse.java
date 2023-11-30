package com.lovely4k.backend.diary.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import com.lovely4k.backend.location.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WebDiaryDetailResponse(
    Long kakaoMapId,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate datingDay,
    Integer score,
    Category category,
    String myText,
    String opponentText,
    PhotoListResponse pictures,
    String placeName,
    BigDecimal latitude,
    BigDecimal longitude

) {
    public static WebDiaryDetailResponse from(DiaryDetailResponse diaryDetailResponse) {
        if (diaryDetailResponse == null) {
            return null;
        }

        return new WebDiaryDetailResponse(diaryDetailResponse.kakaoMapId(), diaryDetailResponse.datingDay(),
            diaryDetailResponse.score(), diaryDetailResponse.category(), diaryDetailResponse.myText(), diaryDetailResponse.opponentText(),
            buildPhotoListResponse(diaryDetailResponse),
            diaryDetailResponse.placeName(), diaryDetailResponse.latitude(), diaryDetailResponse.longitude()
        );
    }

    private static PhotoListResponse buildPhotoListResponse(DiaryDetailResponse diaryDetailResponse) {
        return PhotoListResponse.builder()
            .firstImage(diaryDetailResponse.firstImage())
            .secondImage(diaryDetailResponse.secondImage())
            .thirdImage(diaryDetailResponse.thirdImage())
            .fourthImage(diaryDetailResponse.fourthImage())
            .fifthImage(diaryDetailResponse.fifthImage())
            .build();
    }
}
