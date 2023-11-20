package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Diary;

import java.math.BigDecimal;

public record DiaryGridResponse(
    Long kakaoMapId,
    BigDecimal latitude,
    BigDecimal longitude,
    String placeName,
    Long diaryId
) {
    public static DiaryGridResponse from(Diary diary) {
        return new DiaryGridResponse(diary.getLocation().getKakaoMapId(), diary.getLocation().getLatitude(), diary.getLocation().getLongitude(), diary.getLocation().getPlaceName(), diary.getId());
    }
}
