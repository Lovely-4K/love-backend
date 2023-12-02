package com.lovely4k.backend.common.cache;

import java.util.List;

public record CacheEvictedEvent(
    String prefix,
    List<String> cacheNames
) {
}
