package com.lovely4k.backend.diary;

import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.response.DiaryListResponse;
import com.lovely4k.backend.location.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class DiaryQueryRepository {

    private final DiaryRepository diaryRepository;
    private final QDiaryRepository qDiaryRepository;

    public DiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {
        return diaryRepository.findDiaryDetail(diaryId, coupleId, memberId);
    }

    public Page<DiaryListResponse> findDiaryList(Long coupleId, Category category, Pageable pageable) {
        return qDiaryRepository.findDiaryList(coupleId, category, pageable);
    }

}
