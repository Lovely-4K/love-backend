package com.lovely4k.backend.diary.controller.request;

import java.util.List;

public record DiaryDeleteRequest(
    List<Long> diaryList
) {
}
