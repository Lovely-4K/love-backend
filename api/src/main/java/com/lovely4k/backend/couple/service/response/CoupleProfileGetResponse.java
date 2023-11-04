package com.lovely4k.backend.couple.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.member.Member;

import java.time.LocalDate;

public record CoupleProfileGetResponse(
    String boyNickname,
    String boyMbti,
    String girlNickname,
    String girlMbti,
    String boyImageUrl,
    String girlImageUrl,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate meetDay
) {
    public static CoupleProfileGetResponse from(Member boy, Member girl, LocalDate meetDay) {
        return new CoupleProfileGetResponse(
            boy.getNickname(),
            boy.getMbti(),
            girl.getNickname(),
            girl.getMbti(),
            boy.getImageUrl(),
            girl.getImageUrl(),
            meetDay);
    }
}
