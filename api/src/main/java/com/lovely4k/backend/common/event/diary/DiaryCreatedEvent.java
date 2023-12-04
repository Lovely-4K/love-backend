package com.lovely4k.backend.common.event.diary;

import java.util.List;

public class DiaryCreatedEvent implements DiaryEvent{
    private Long coupleId;
    private String prefix;
    private List<String> cacheNames;

    public DiaryCreatedEvent(Long coupleId, String prefix, List<String> cacheNames) {
        this.coupleId = coupleId;
        this.prefix = prefix;
        this.cacheNames = cacheNames;
    }


    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public List<String> getCacheNames() {
        return this.cacheNames;
    }

    public Long getCoupleId() {
        return this.coupleId;
    }
}
