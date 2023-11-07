package com.lovely4k.backend.member.authentication;

import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;


public record SessionUser (
    Long memberId,
    Long coupleId,
    Sex sex,
    String nickName,
    String picture

)  {
    public static SessionUser from(Member member) {
        return new SessionUser(member.getId(), member.getCoupleId(), member.getSex(), member.getNickname(), member.getImageUrl());
    }
}
