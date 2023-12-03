package com.lovely4k.backend.member.event;

import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class MemberCreatedEventListener {

    private final CacheManager cacheManager;

    @Async
    @TransactionalEventListener(value = MemberUpdatedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterMemberUpdateInvalidateCache(MemberUpdatedEvent event) {
        Member member = event.member();
        Objects.requireNonNull(cacheManager.getCache(CacheConstants.USER_DETAILS)).evict(member.getEmail());
    }
}