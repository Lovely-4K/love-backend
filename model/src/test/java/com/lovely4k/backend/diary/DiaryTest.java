package com.lovely4k.backend.diary;

import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class DiaryTest {

    @DisplayName("다이어리의 create 메서드를 통해 다이어리를 생성할 수 있다. 회원의 성별을 통해 text는 자동 생성된다.")
    @Test
    void createBoy() {
        // given
        Integer score = 0;
        LocalDate localDate = LocalDate.of(2023, 10, 20);
        String text = "안녕하세요";
        Member member = Member.builder()
                .name("tommy")
                .sex("boy")
                .coupleId(1L)
                .build();

        Location location = Location.create(2L, "경기도 고양시", Category.FOOD);

        // when
        Diary diary = Diary.create(score, localDate, text, member, location);

        // then
        assertAll(
                () -> assertThat(diary.getBoyText()).isNotNull().isEqualTo("안녕하세요"),
                () -> assertThat(diary.getScore()).isZero(),
                () -> assertThat(diary.getCoupleId()).isEqualTo(member.getCoupleId()),
                () -> assertThat(diary.getDatingDay()).isEqualTo(localDate),
                () -> assertThat(diary.getLocation()).isEqualTo(location)
        );
    }

    @DisplayName("다이어리의 create 메서드를 통해 다이어리를 생성할 수 있다. 회원의 성별을 통해 text는 자동 생성된다.")
    @Test
    void createGirl() {
        // given
        Integer score = 0;
        LocalDate localDate = LocalDate.of(2023, 10, 20);
        String text = "안녕하세요";
        Member member = Member.builder()
                .name("tommy")
                .sex("girl")
                .coupleId(1L)
                .build();

        Location location = Location.create(2L, "경기도 고양시", Category.FOOD);

        // when
        Diary diary = Diary.create(score, localDate, text, member, location);

        // then
        assertAll(
                () -> assertThat(diary.getGirlText()).isNotNull().isEqualTo("안녕하세요"),
                () -> assertThat(diary.getScore()).isZero(),
                () -> assertThat(diary.getCoupleId()).isEqualTo(member.getCoupleId()),
                () -> assertThat(diary.getDatingDay()).isEqualTo(localDate),
                () -> assertThat(diary.getLocation()).isEqualTo(location)
        );
    }

    @DisplayName("다이어리 평점이 0 이하인 경우 IllegalArgumentException이 발생한다.")
    @Test
    void scoreUnderRange() {
        // given
        Integer score = -1;
        LocalDate localDate = LocalDate.of(2023, 10, 20);
        String text = "안녕하세요";
        Member member = Member.builder()
                .name("tommy")
                .sex("boy")
                .build();

        Location location = Location.create(2L, "경기도 고양시", Category.FOOD);

        // when && then
        assertThatThrownBy(
                () -> Diary.create(score, localDate, text, member, location)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score out of range.");

    }

    @DisplayName("다이어리 평점이 5를 넘어가는 경우 IllegalArgumentException이 발생한다.")
    @Test
    void scoreOverRange() {
        // given
        Integer score = 6;
        LocalDate localDate = LocalDate.of(2023, 10, 20);
        String text = "안녕하세요";
        Member member = Member.builder()
                .name("tommy")
                .sex("boy")
                .build();

        Location location = Location.create(2L, "경기도 고양시", Category.FOOD);

        // when && then
        assertThatThrownBy(
                () -> Diary.create(score, localDate, text, member, location)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score out of range.");

    }

    @DisplayName("다른 커플의 내용을 확인하려고 하는 경우 IllegalArgumentException이 발생한다.")
    @Test
    void checkAuthority() {
        // given
        Member member = Member.builder()
                .sex("boy")
                .coupleId(1L)
                .build();

        Diary diary = Diary.builder()
                .coupleId(2L)
                .boyText("hello")
                .girlText("hi")
                .build();

        Long memberCoupleId = member.getCoupleId();
        // when && then
        assertThatThrownBy(
                () -> diary.checkAuthority(memberCoupleId)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("you can only see your couple's diary");
    }

}
