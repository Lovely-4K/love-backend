package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.diary.DiaryQueryRepository;
import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryQueryService {

    private final DiaryQueryRepository repository;

    public WebDiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {

        DiaryDetailResponse diaryDetailResponse = repository.findDiaryDetail(diaryId, coupleId, memberId);
        if (diaryDetailResponse == null) {
            return null;
        }
        return WebDiaryDetailResponse.from(diaryDetailResponse);
    }
}
