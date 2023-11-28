package com.lovely4k.backend.diary;

import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryAdapter {

    private final DiaryRepository diaryRepository;


    public Diary save(Diary diary) {
        return diaryRepository.save(diary);
    }

    public Optional<Diary> findById(Long diaryId) {
        return diaryRepository.findById(diaryId);
    }


    public List<Diary> findDiaryList(BigDecimal rLatitude, BigDecimal rLongitude, BigDecimal lLatitude, BigDecimal lLongitude, Long coupleId) {
        return diaryRepository.findInGrid(rLatitude, rLongitude, lLatitude, lLongitude, coupleId);
    }

    public void delete(Diary diary) {
        diaryRepository.delete(diary);
    }

    public void deleteAll(List<Diary> diaries) {
        diaryRepository.deleteAll(diaries);
    }

    public DiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {
        return diaryRepository.findDiaryDetail(diaryId, coupleId, memberId);
    }
}
