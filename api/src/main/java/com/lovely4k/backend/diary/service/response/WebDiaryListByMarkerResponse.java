package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Diary;

import java.math.BigDecimal;
import java.util.List;

public record WebDiaryListByMarkerResponse(
    BigDecimal latitude,
    BigDecimal longitude,
    String placeName,
    List<WebDiaryMarkerResponse> diaries
) {

    public static WebDiaryListByMarkerResponse from(List<Diary> diaries) {
        if (diaries.isEmpty()) {
            return null;
        }

        Diary diary = diaries.get(0);
        return new WebDiaryListByMarkerResponse(
            diary.getLocation().getLatitude(),
            diary.getLocation().getLongitude(),
            diary.getLocation().getPlaceName(),
            diaries.stream().map(WebDiaryMarkerResponse::from).toList());
    }

}
