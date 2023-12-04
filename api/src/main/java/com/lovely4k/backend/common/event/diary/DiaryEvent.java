package com.lovely4k.backend.common.event.diary;

import java.util.List;

public interface DiaryEvent {

    String getPrefix();
    List<String> getCacheNames();
}
