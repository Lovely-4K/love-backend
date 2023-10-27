package com.lovely4k.backend.couple.service.response;

import com.lovely4k.backend.member.Member;

public record CoupleProfileGetResponse(
    String boyNickname,
    String boyMbti,
    String girlNickname,
    String girlMbti
) {
    public static CoupleProfileGetResponse fromEntity(Member boy, Member girl) {
        return new CoupleProfileGetResponse(boy.getNickname(), boy.getMbti(), girl.getNickname(), girl.getMbti());
    }
}
