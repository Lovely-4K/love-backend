package com.lovely4k.backend.diary.service.request;

import java.time.LocalDate;
import java.util.List;

public record DiaryEditRequest(
    Integer score,
    LocalDate datingDay,
    String category,
    String text,
    List<String> images
) {
}
