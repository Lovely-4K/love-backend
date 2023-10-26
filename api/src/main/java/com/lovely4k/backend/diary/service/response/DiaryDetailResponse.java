package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.location.Category;

import java.time.LocalDate;

public record DiaryDetailResponse(
        Long kakaoMapId,
        LocalDate datingDay,
        Integer score,
        Category category,
        String boyText,
        String girlText,
        PhotoList pictures
) {
    public static DiaryDetailResponse of(Diary diary) {
        return new DiaryDetailResponse(diary.getLocation().getKakaoMapId(), diary.getDatingDay(), diary.getScore(),
                diary.getLocation().getCategory(), diary.getBoyText(), diary.getGirlText(),
                PhotoList.from(diary.getPhotos()));
    }
}
