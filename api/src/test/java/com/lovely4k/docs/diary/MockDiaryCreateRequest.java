package com.lovely4k.docs.diary;

public record MockDiaryCreateRequest(
        String kakaoMapId,
        String address,
        Integer score,
        String datingDay,
        String category,
        String text
) {
}
