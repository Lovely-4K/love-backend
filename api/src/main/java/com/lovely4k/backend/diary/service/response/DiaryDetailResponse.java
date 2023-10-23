package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.location.Category;

import java.time.LocalDate;

public record DiaryDetailResponse(
        Long kakaoId,
        LocalDate datingDay,
        Integer score,
        Category category,
        String boyText,
        String girlText
) {

}
