package com.lovely4k.backend.diary;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiaryQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    DiaryQueryRepository diaryQueryRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @AfterEach
    void tearDown() {
        diaryRepository.deleteAllInBatch();
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
        List<Diary> diaries = diaryQueryRepository.findByMarker(2L, 1L);

        // then
        assertThat(diaries).hasSize(2);
    }

    @DisplayName("findByMarker 조회 시 만족하는 결과가 없다면 Empty이다.")
    @Test
    void findByMarker_NoMatch() {
        // given
        Diary diary1 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 2L);
        Diary diary2 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 2L);
        Diary diary3 = buildDiaryWithLocationId(Category.ACCOMODATION, 2L, 2L);
        Diary diary4 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 3L);
        diaryRepository.saveAll(List.of(diary1, diary2, diary3, diary4));

        // when
        List<Diary> diaries = diaryQueryRepository.findByMarker(3L, 2L);

        // then
        assertThat(diaries).isEmpty();
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