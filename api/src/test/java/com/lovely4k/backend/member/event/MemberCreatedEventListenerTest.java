package com.lovely4k.backend.member.event;

import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberCreatedEventListenerTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private MemberCreatedEventListener listener;

    @DisplayName("회원 정보가 수정되면 토큰 값을 검증 할 때 캐시가 아닌 db에서 검증한다.")
    @Test
    void handleAfterMemberUpdateInvalidateCache() {
        // Given
        Member member = Member.builder().email("test@test.com").build();
        MemberUpdatedEvent event = new MemberUpdatedEvent(member);

        given(cacheManager.getCache(CacheConstants.USER_DETAILS)).willReturn(cache);

        // When
        listener.handleAfterMemberUpdateInvalidateCache(event);

        // Then
        verify(cacheManager).getCache(CacheConstants.USER_DETAILS);
        verify(cache).evict(member.getEmail());
    }
}