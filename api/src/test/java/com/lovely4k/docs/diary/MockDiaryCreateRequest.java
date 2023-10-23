package com.lovely4k.docs.diary;

public record MockDiaryCreateRequest(
        String kakaoId,
        String location,
        Integer score,
        String datingDay,
        String category,
        String text
) {
}
