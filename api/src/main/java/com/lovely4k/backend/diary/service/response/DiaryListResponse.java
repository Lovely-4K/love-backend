package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Diary;

public record DiaryListResponse(
        Long diaryId,
        Long kakaoMapId,
        String imageUrl
) {

    public static DiaryListResponse from(Diary diary) {
        if (diary.getPhotos() == null) {
            return new DiaryListResponse(diary.getId(), diary.getLocation().getKakaoMapId(), null);
        }
        return new DiaryListResponse(diary.getId(), diary.getLocation().getKakaoMapId(), diary.getPhotos().getFirstImage());
    }
}
