package com.lovely4k.backend.couple.repository.response;

import com.lovely4k.backend.couple.CoupleStatus;

import java.time.LocalDate;

public record FindCoupleProfileResponse(
    String myNickname,
    String myMbti,
    String myImageUrl,
    Long myId,
    String myCalendarColor,
    LocalDate myBirthday,
    String opponentNickname,
    String opponentMbti,
    String opponentImageUrl,
    Long opponentId,
    LocalDate opponentBirthday,
    String opponentCalendarColor,
    LocalDate meetDay,
    CoupleStatus coupleStatus
) {
}
