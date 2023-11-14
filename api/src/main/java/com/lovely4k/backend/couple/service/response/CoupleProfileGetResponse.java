package com.lovely4k.backend.couple.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.member.Member;

import java.time.LocalDate;
import java.util.Optional;

public record CoupleProfileGetResponse(
    String myNickname,
    String myMbti,
    String myImageUrl,
    Long myId,
    String myCalendarColor,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate myBirthday,

    String opponentNickname,
    String opponentMbti,
    String opponentImageUrl,
    Long opponentId,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate opponentBirthday,
    String opponentCalendarColor,

    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate meetDay
) {
    public static CoupleProfileGetResponse from(Member my, Optional<Member> opponent, LocalDate meetDay) {
        return new CoupleProfileGetResponse(
            my.getNickname(),
            my.getMbti(),
            my.getImageUrl(),
            my.getId(),
            my.getCalendarColor(),
            my.getBirthday(),

            opponent.map(Member::getNickname).orElse(null),
            opponent.map(Member::getMbti).orElse(null),
            opponent.map(Member::getImageUrl).orElse(null),
            opponent.map(Member::getId).orElse(null),
            opponent.map(Member::getBirthday).orElse(null),
            opponent.map(Member::getCalendarColor).orElse(null),

            meetDay
        );
    }
}