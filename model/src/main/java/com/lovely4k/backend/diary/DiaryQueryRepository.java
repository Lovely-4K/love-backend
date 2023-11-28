package com.lovely4k.backend.diary;

import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class DiaryQueryRepository {

    private final DiaryRepository diaryRepository;

    public DiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {
        return diaryRepository.findDiaryDetail(diaryId, coupleId, memberId);
    }
}
