package com.lovely4k.backend.diary.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.diary.Diary;

import java.time.LocalDate;

public record DiaryListResponse(
        Long diaryId,
        Long kakaoMapId,
        String imageUrl,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate datingDay,
        String placeName
) {

    public static DiaryListResponse from(Diary diary) {
        if (diary.getPhotos() == null) {
            return new DiaryListResponse(diary.getId(), diary.getLocation().getKakaoMapId(), null, diary.getDatingDay(), diary.getLocation().getPlaceName());
        }
        return new DiaryListResponse(diary.getId(), diary.getLocation().getKakaoMapId(), diary.getPhotos().getFirstImage(), diary.getDatingDay(), diary.getLocation().getPlaceName());
    }
}
