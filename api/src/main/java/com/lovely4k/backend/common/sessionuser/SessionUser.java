package com.lovely4k.backend.common.sessionuser;


public record SessionUser (
    Long memberId,
    Long coupleId,
    String sex,
    String nickName,
    String picture

)  {

    public static SessionUser from(MemberInfo memberInfo) {
        return new SessionUser(memberInfo.getMemberId(), memberInfo.getCoupleId(), memberInfo.getSex(), memberInfo.getNickName(), memberInfo.getPicture());
    }
}