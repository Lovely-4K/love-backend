package com.lovely4k.backend.diary.controller.request;

import java.time.LocalDate;

public record DiaryEditRequest(
        String kakaoId,
        String location,
        Integer score,
        LocalDate datingDay,
        String category,
        String text
) {
}
