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
    void findDiaryList() {
        // given
        Location food = Location.create(1L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, Category.FOOD);
        Location accomodation = Location.create(1L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, Category.ACCOMODATION);
        Diary diary1 = Diary.builder()
                .coupleId(1L)
                .girlText("girl text")
                .boyText("boy text")
                .location(food)
                .build();

        Diary diary2 = Diary.builder()
                .coupleId(1L)
                .girlText("girl text")
                .boyText("boy text")
                .location(accomodation)
                .build();

        diaryRepository.saveAll(List.of(diary1, diary2));
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        // when
        Page<Diary> diaryPage = diaryRepositoryAdapter.findDiaryList(1L, Category.FOOD, pageRequest);

        // then
        System.out.println(diaryPage);
        assertAll(
                () -> assertThat(diaryPage.getNumberOfElements()).isEqualTo(1),
                () -> assertThat(diaryPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(diaryPage.getPageable().isPaged()).isTrue()
        );

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
}