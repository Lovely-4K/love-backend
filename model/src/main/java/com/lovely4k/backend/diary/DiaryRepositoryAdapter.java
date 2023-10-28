package com.lovely4k.backend.diary;

import com.lovely4k.backend.location.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryAdapter {

    private final DiaryRepository diaryRepository;
    private final QDiaryRepository qDiaryRepository;


    public Diary save(Diary diary) {
        return diaryRepository.save(diary);
    }

    public Optional<Diary> findById(Long diaryId) {
        return diaryRepository.findById(diaryId);
    }

    public Page<Diary> findDiaryList(Long coupleId, Category category, Pageable pageable) {
        return qDiaryRepository.findAll(coupleId, category, pageable);
    }

    public void delete(Diary diary) {
        diaryRepository.delete(diary);
    }
}
