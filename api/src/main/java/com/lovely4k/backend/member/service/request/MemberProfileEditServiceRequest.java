package com.lovely4k.backend.member.service.request;

import java.time.LocalDate;

public record MemberProfileEditServiceRequest(
    String nickname,
    LocalDate birthday,
    String mbti,
    String calendarColor
) {
}
