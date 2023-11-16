package com.lovely4k.docs.diary;

import java.math.BigDecimal;

public record MockDiaryCreateRequest(
        Long kakaoMapId,
        String address,
        String placeName,
        Integer score,
        String datingDay,
        String category,
        BigDecimal latitude,
        BigDecimal longitude,
        String text
) {
}
