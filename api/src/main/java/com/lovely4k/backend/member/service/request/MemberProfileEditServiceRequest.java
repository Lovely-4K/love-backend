package com.lovely4k.backend.member.service.request;

import com.lovely4k.backend.member.Sex;

import java.time.LocalDate;

public record MemberProfileEditServiceRequest(
    Sex sex,
    String imageUrl,
    String name,
    String nickname,
    LocalDate birthday,
    String mbti,
    String calendarColor
) {
}
