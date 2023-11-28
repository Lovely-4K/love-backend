package com.lovely4k.backend.diary;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DiaryRepositoryAdapterTest extends IntegrationTestSupport {

    @Autowired
    DiaryRepositoryAdapter diaryRepositoryAdapter;

    @Autowired
    DiaryRepository diaryRepository;

    @AfterEach
    void tearDown() {
        diaryRepository.deleteAllInBatch();
    }

    @DisplayName("save를 통해 Diary를 저장할 수 있다. ")
    @Test
    void save() {
        // given
        Diary diary = Diary.builder()
                .coupleId(1L)
                .girlText("girl text")
                .boyText("boy text")
                .build();

        // when
        Diary savedDiary = diaryRepositoryAdapter.save(diary);

        // then
        assertThat(diaryRepository.findById(savedDiary.getId()).orElseThrow().getId()).isEqualTo(savedDiary.getId());
    }


    @Test
    void findById() {
        // given
        Diary diary = Diary.builder()
                .coupleId(1L)
                .girlText("girl text")
                .boyText("boy text")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        // when
        Diary findDiary = diaryRepositoryAdapter.findById(savedDiary.getId()).orElseThrow();

        // then
        assertThat(findDiary.getId()).isEqualTo(savedDiary.getId());
    }

    @Test
    void delete() {
        // given
        Diary diary = Diary.builder()
                .coupleId(1L)
                .girlText("girl text")
                .boyText("boy text")
                .build();
        Diary savedDiary = diaryRepository.save(diary);

        // when
        diaryRepositoryAdapter.delete(diary);

        // then
        assertThat(diaryRepository.findById(savedDiary.getId())).isEmpty();
    }

    @DisplayName("findByMarker를 통해 kakaoMapId에 해당하는 다이어리 목록들을 조회할 수 있다.")
    @Test
    void findByMarker() {
        // given
        Diary diary1 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 2L);
        Diary diary2 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 2L);
        Diary diary3 = buildDiaryWithLocationId(Category.ACCOMODATION, 2L, 2L);
        Diary diary4 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 3L);
        diaryRepository.saveAll(List.of(diary1, diary2, diary3, diary4));

        // when
        List<Diary> diaries = diaryRepositoryAdapter.findByMarker(2L, 1L);

        // then
        assertThat(diaries).hasSize(2);
    }

    private static Diary buildDiaryWithLocationId(Category category, long coupleId, long kakaoMapId) {
        return Diary.builder()
            .coupleId(coupleId)
            .location(Location.create(kakaoMapId, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, category))
            .boyText("hello")
            .girlText("hi")
            .score(4)
            .photos(Photos.builder().firstImage("test-image1").build())
            .datingDay(LocalDate.of(2023, 10, 20))
            .build();
    }
}