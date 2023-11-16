package com.lovely4k.backend.diary;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class QDiaryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    QDiaryRepository qDiaryRepository;

    @AfterEach
    void tearDown() {
        diaryRepository.deleteAllInBatch();
    }

    @DisplayName("findAll method를 통해 Page 정보를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Diary foodDiary1 = buildDiary(Category.FOOD, 1L);
        Diary foodDiary2 = buildDiary(Category.FOOD, 1L);
        Diary accomodation1 = buildDiary(Category.ACCOMODATION, 1L);
        Diary accomodation2 = buildDiary(Category.ACCOMODATION, 1L);
        diaryRepository.saveAll(List.of(foodDiary1, foodDiary2, accomodation1, accomodation2));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdDate").ascending());
        // when
        Page<Diary> diaryPage = qDiaryRepository.findAll(1L, Category.FOOD, pageRequest);

        // then
        assertThat(diaryPage.getNumberOfElements()).isEqualTo(2);
    }

    @DisplayName("findAll method를 사용할 때 category가 없으면 모든 다이어리를 조회해온다.")
    @Test
    void findAllNoCategory() {
        // given
        Diary foodDiary1 = buildDiary(Category.FOOD, 1L);
        Diary foodDiary2 = buildDiary(Category.FOOD, 1L);
        Diary accomodation1 = buildDiary(Category.ACCOMODATION, 1L);
        Diary accomodation2 = buildDiary(Category.ACCOMODATION, 1L);
        diaryRepository.saveAll(List.of(foodDiary1, foodDiary2, accomodation1, accomodation2));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdDate").ascending());
        // when
        Page<Diary> diaryPage = qDiaryRepository.findAll(1L, null, pageRequest);

        // then
        assertThat(diaryPage.getNumberOfElements()).isEqualTo(4);

    }

    @DisplayName("findAll method를 사용할 때 couple id가 없으면 예외가 발생한다.")
    @Test
    void findAllNoCoupleId() {
        // given
        Diary foodDiary1 = buildDiary(Category.FOOD, 1L);
        Diary foodDiary2 = buildDiary(Category.FOOD, 1L);
        Diary accomodation1 = buildDiary(Category.ACCOMODATION, 1L);
        Diary accomodation2 = buildDiary(Category.ACCOMODATION, 1L);
        diaryRepository.saveAll(List.of(foodDiary1, foodDiary2, accomodation1, accomodation2));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("localDateTime").ascending());

        // when && then
        assertThatThrownBy(
                () -> qDiaryRepository.findAll(null, Category.FOOD, pageRequest)
        ).isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessage("couple id must not be null");

    }

    private static Diary buildDiary(Category category, long coupleId) {
        return Diary.builder()
            .location(Location.create(102L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, category))
            .coupleId(coupleId)
            .boyText("boy text")
            .girlText("girl text")
            .score(5)
            .datingDay(LocalDate.of(2023, 10, 20))
            .photos(Photos.builder().firstImage("first-image").secondImage("second-image").build())
            .build();
    }
}