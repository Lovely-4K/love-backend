package com.lovely4k.backend.diary;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DiaryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    DiaryRepository diaryRepository;

    @Transactional
    @DisplayName("findInGrid를 통해서 특정 위치 안에 존재하는 다이어리의 목록을 조회할 수 있다. ")
    @Test
    void findInGrid() {
        // given
        Diary diary1 = Diary.builder()
            .coupleId(1L)
            .location(Location.create(1L, "서울시", "서울역", BigDecimal.valueOf(37.5563), BigDecimal.valueOf(126.9723), Category.ETC))
            .boyText("hello")
            .girlText("hi")
            .score(4)
            .photos(Photos.builder().firstImage("test-image1").build())
            .datingDay(LocalDate.of(2023, 10, 20))
            .build();

        Diary diary2 = Diary.builder()
            .coupleId(1L)
            .location(Location.create(1098L, "부산시", "부산역", BigDecimal.valueOf(35.1151), BigDecimal.valueOf(129.0422), Category.ETC))
            .boyText("hello")
            .girlText("hi")
            .score(3)
            .photos(Photos.builder().firstImage("test-image1").build())
            .datingDay(LocalDate.of(2023, 10, 20))
            .build();

        diaryRepository.saveAll(List.of(diary1, diary2));

        // when
        List<Diary> diaries = diaryRepository.findInGrid(BigDecimal.valueOf(38), BigDecimal.valueOf(127), BigDecimal.valueOf(36), BigDecimal.valueOf(126), 1L);

        // then
        assertAll(
            () -> assertThat(diaries).hasSize(1),
            () -> assertThat(diaries.get(0).getLocation().getAddress()).isEqualTo("서울시"),
            () -> assertThat(diaries.get(0).getLocation().getKakaoMapId()).isEqualTo(1L),
            () -> assertThat(diaries.get(0).getLocation().getCategory()).isEqualTo(Category.ETC),
            () -> assertThat(diaries.get(0).getId()).isEqualTo(diary1.getId())
        );
    }

}