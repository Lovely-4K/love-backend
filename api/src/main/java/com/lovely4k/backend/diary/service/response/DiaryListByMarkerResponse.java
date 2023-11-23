package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Diary;

import java.math.BigDecimal;
import java.util.List;

public record DiaryListByMarkerResponse(
    BigDecimal latitude,
    BigDecimal longitude,
    String placeName,
    List<DiaryMarkerResponse> diaries
) {

    public static DiaryListByMarkerResponse from(Diary diary, List<DiaryMarkerResponse> diaryMarkerResponses) {
        return new DiaryListByMarkerResponse(diary.getLocation().getLatitude(), diary.getLocation().getLongitude(), diary.getLocation().getPlaceName(), diaryMarkerResponses);
    }

    public static DiaryListByMarkerResponse emptyValue() {
        return new DiaryListByMarkerResponse(null, null, null, null);
    }
}
