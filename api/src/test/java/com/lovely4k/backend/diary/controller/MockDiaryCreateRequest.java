package com.lovely4k.backend.diary.controller;

public record MockDiaryCreateRequest(
        String kakaoId,
        String location,
        Integer score,
        String datingDay,
        String category,
        String text
) {
}
