package com.lovely4k.backend.diary.service.response;

import java.util.List;

public record DiaryListByMarkerResponse(
    List<DiaryMarkerResponse> diaries
) {

    public static DiaryListByMarkerResponse from(List<DiaryMarkerResponse> diaryMarkerResponses) {
        return new DiaryListByMarkerResponse(diaryMarkerResponses);
    }

    public static DiaryListByMarkerResponse emptyValue() {
        return new DiaryListByMarkerResponse(null);
    }
}
