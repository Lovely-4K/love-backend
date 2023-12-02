package com.lovely4k.backend.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CustomCacheManager {

    private final CacheManager cacheManager;

    @Async
    @EventListener(CacheEvictedEvent.class)
    public void evictCacheStartingWith(CacheEvictedEvent event) {
        for (String cacheName : event.cacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

                Set<Object> keysToDelete = nativeCache.asMap().keySet().stream()
                    .filter(key -> key.toString().startsWith(event.prefix()))
                    .collect(Collectors.toSet());

                keysToDelete.forEach(nativeCache::invalidate);
            }
        }
    }


}
