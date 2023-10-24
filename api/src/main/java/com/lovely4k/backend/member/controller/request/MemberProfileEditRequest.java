package com.lovely4k.backend.member.controller.request;

import java.time.LocalDate;

public record MemberProfileEditRequest(
    String sex,
    String imageUrl,
    String name,
    String nickname,
    LocalDate birthday,
    String mbti,
    String calendarColor
) {
}
