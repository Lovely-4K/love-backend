package com.lovely4k.backend.member.service.response;

import java.time.LocalDate;

public record MemberProfileGetResponse(
    String sex,
    String imageUrl,
    String name,
    String nickname,
    LocalDate birthday,
    String mbti,
    String calendarColor
) {
}
