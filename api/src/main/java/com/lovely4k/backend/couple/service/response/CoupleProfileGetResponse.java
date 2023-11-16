package com.lovely4k.backend.couple.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.couple.repository.response.FindCoupleProfileResponse;

import java.time.LocalDate;

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
    public static CoupleProfileGetResponse from(FindCoupleProfileResponse response) {
        return new CoupleProfileGetResponse(
            response.myNickname(),
            response.myMbti(),
            response.myImageUrl(),
            response.myId(),
            response.myCalendarColor(),
            response.myBirthday(),
            response.opponentNickname(),
            response.opponentMbti(),
            response.opponentImageUrl(),
            response.opponentId(),
            response.opponentBirthday(),
            response.opponentCalendarColor(),
            response.meetDay()
        );
    }
}