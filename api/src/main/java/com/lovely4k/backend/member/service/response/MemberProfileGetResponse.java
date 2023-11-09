package com.lovely4k.backend.member.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.member.Member;

import java.time.LocalDate;

public record MemberProfileGetResponse(
    String imageUrl,
    String nickname,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday,
    String mbti,
    String calendarColor
) {
    public static MemberProfileGetResponse of(Member member) {
        return new MemberProfileGetResponse(
            member.getImageUrl(),
            member.getNickname(),
            member.getBirthday(),
            member.getMbti(),
            member.getCalendarColor()
        );
    }
}
