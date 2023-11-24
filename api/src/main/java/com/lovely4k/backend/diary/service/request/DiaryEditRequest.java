package com.lovely4k.backend.diary.service.request;

import java.time.LocalDate;

public record DiaryEditRequest(
    Integer score,
    LocalDate datingDay,
    String category,
    String myText,
    String opponentText
) {
}
