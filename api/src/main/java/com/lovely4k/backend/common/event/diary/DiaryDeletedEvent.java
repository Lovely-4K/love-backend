package com.lovely4k.backend.common.event.diary;

import java.util.List;

public class DiaryDeletedEvent implements DiaryEvent{

    private String prefix;
    private List<String> cacheNames;

    public DiaryDeletedEvent(String prefix, List<String> cacheNames) {
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
}
