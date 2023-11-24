package com.lovely4k.docs.diary;

public record MockDiaryEditRequest(
    Integer score,
    String datingDay,
    String category,
    String myText,
    String opponentText
) {
}
