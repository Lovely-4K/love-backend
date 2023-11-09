package com.lovely4k.backend.common.sessionuser;


public record SessionUser (
    Long memberId,
    Long coupleId,
    String sex,
    String nickName,
    String picture

)  {

    public static SessionUser from(UserDetails userDetails) {
        return new SessionUser(userDetails.getMemberId(), userDetails.getCoupleId(), userDetails.getSex(), userDetails.getNickName(), userDetails.getPicture());
    }
}