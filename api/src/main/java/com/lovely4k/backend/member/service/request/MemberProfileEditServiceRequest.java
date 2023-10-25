package com.lovely4k.backend.member.service.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MemberProfileEditServiceRequest(
    String sex,
    String imageUrl,
    String name,
    String nickname,
    LocalDate birthday,
    String mbti,
    String calendarColor
) {
}
