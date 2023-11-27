package com.lovely4k.backend.diary.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.diary.Diary;

import java.time.LocalDate;

public record DiaryMarkerResponse(
    Long diaryId,
    String imageUrl,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate datingDay
) {

    public static DiaryMarkerResponse from(Diary diary) {
        if (diary.getPhotos() == null) {
            return new DiaryMarkerResponse(diary.getId(), null, diary.getDatingDay());
        } else {
            return new DiaryMarkerResponse(diary.getId(), diary.getPhotos().getFirstImage(), diary.getDatingDay());
        }
    }
}
