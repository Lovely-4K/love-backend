package com.lovely4k.backend.diary.response;

import com.lovely4k.backend.location.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiaryDetailResponse(
    Long kakaoMapId,
    LocalDate datingDay,
    Integer score,
    Category category,
    String myText,
    String opponentText,
    String firstImage,
    String secondImage,
    String thirdImage,
    String fourthImage,
    String fifthImage,
    String placeName,
    BigDecimal latitude,
    BigDecimal longitude
) {
}
