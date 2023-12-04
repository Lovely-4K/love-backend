package com.lovely4k.backend.common.cache;

import com.lovely4k.backend.common.event.diary.DiaryCreatedEvent;
import com.lovely4k.backend.common.event.diary.DiaryDeletedEvent;
import com.lovely4k.backend.common.event.diary.DiaryEditedEvent;
import com.lovely4k.backend.common.event.diary.DiaryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Profile("!test")
@RequiredArgsConstructor
@Component
public class CustomCacheManager {

    private final CacheManager cacheManager;

    @Async
    @EventListener(classes = {DiaryCreatedEvent.class, DiaryEditedEvent.class, DiaryDeletedEvent.class})
    public void evictCacheDiaryCreated(DiaryEvent event) {
        for (String cacheName : event.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

                Set<Object> keysToDelete = nativeCache.asMap().keySet().stream()
                    .filter(key -> key.toString().startsWith(event.getPrefix()))
                    .collect(Collectors.toSet());

                keysToDelete.forEach(nativeCache::invalidate);
            }
        }
    }

}
