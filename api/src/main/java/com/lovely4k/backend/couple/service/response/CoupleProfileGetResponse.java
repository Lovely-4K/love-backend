package com.lovely4k.backend.couple.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.member.Member;

import java.time.LocalDate;
import java.util.Optional;

public record CoupleProfileGetResponse(
    String boyNickname,
    String boyMbti,
    String boyImageUrl,
    Long boyId,
    String girlNickname,
    String girlMbti,
    String girlImageUrl,
    Long girlId,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate meetDay
) {
    public static CoupleProfileGetResponse from(Optional<Member> boy, Optional<Member> girl, LocalDate meetDay) {
        return new CoupleProfileGetResponse(
            boy.map(Member::getNickname).orElse(null),
            boy.map(Member::getMbti).orElse(null),
            boy.map(Member::getImageUrl).orElse(null),
            boy.map(Member::getId).orElse(null),
            girl.map(Member::getNickname).orElse(null),
            girl.map(Member::getMbti).orElse(null),
            girl.map(Member::getImageUrl).orElse(null),
            girl.map(Member::getId).orElse(null),
            meetDay
        );
    }
}
