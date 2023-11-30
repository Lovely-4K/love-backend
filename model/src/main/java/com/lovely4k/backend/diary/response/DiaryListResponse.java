package com.lovely4k.backend.diary.response;

import com.lovely4k.backend.diary.Diary;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiaryListResponse(
    Long diaryId,
    Long kakaoMapId,
    String imageUrl,
    LocalDate datingDay,
    String placeName,
    String address,
    BigDecimal latitude,
    BigDecimal longitude
) {

    public static DiaryListResponse from(Diary diary) {
        if (diary.getPhotos() == null) {
            return new DiaryListResponse(diary.getId(), diary.getLocation().getKakaoMapId(), null, diary.getDatingDay(), diary.getLocation().getPlaceName(), diary.getLocation().getAddress(), diary.getLocation().getLatitude(), diary.getLocation().getLongitude());
        }
        return new DiaryListResponse(diary.getId(), diary.getLocation().getKakaoMapId(), diary.getPhotos().getFirstImage(), diary.getDatingDay(), diary.getLocation().getPlaceName(), diary.getLocation().getAddress(), diary.getLocation().getLatitude(), diary.getLocation().getLongitude());
    }
}
