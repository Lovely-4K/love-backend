package com.lovely4k.backend.member.event;

import com.lovely4k.backend.member.Member;

public record MemberUpdatedEvent(
    Member member
) {
}