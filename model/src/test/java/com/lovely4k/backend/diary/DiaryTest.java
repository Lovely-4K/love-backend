package com.lovely4k.backend.diary;

import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.lovely4k.backend.member.Sex.FEMALE;
import static com.lovely4k.backend.member.Sex.MALE;
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
                .nickname("tommy")
                .sex(MALE)
                .coupleId(1L)
                .build();

        Location location = Location.create(2L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, Category.FOOD);
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
                .nickname("tommy")
                .sex(FEMALE)
                .coupleId(1L)
                .build();

        Location location = Location.create(2L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, Category.FOOD);

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
                .nickname("tommy")
                .sex(MALE)
                .build();

        Location location = Location.create(2L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, Category.FOOD);

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
                .nickname("tommy")
                .sex(MALE)
                .build();

        Location location = Location.create(2L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ONE, Category.FOOD);

        // when && then
        assertThatThrownBy(
                () -> Diary.create(score, localDate, text, member, location)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score out of range.");

    }

    @DisplayName("다른 커플의 내용을 확인하려고 하는 경우 IllegalArgumentException이 발생한다.")
    @Test
    void checkAuthorityNoAuthority() {
        // given
        Member member = Member.builder()
                .sex(MALE)
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
                .hasMessage("you can only manage your couple's diary");
    }

    @DisplayName("내가 속한 커플의 다이어리에 대해 checkAuthority를 하면 아무 일이 발생하지 않는다. ")
    @Test
    void checkAuthority() {
        // given
        Member member = Member.builder()
                .sex(MALE)
                .coupleId(1L)
                .build();

        Diary diary = Diary.builder()
                .coupleId(1L)
                .boyText("hello")
                .girlText("hi")
                .build();

        Long memberCoupleId = member.getCoupleId();
        // when && then
        diary.checkAuthority(memberCoupleId);
    }

    @DisplayName("addPhoto 메서드를 통해 다이어리에 사진을 추가할 수 있다.")
    @Test
    void addPhoto() {
        // given
        Photos photos = Photos.builder()
                .firstImage("first-image-url")
                .secondImage("second-image-url")
                .build();

        Diary diary = Diary.builder()
                .coupleId(1L)
                .boyText("boy-text")
                .girlText("girl-text")
                .build();

        assertThat(diary.getPhotos()).isNull();
        // when
        diary.addPhoto(photos);

        // then
        assertAll(
                () -> assertThat(diary.getPhotos()).isNotNull(),
                () -> assertThat(diary.getPhotos().countOfImages()).isEqualTo(2)
        );
    }

    @DisplayName("update를 통해 다이어리 관련된 값들을 업데이트 할 수 있다.")
    @Test
    void update_Boy() {
        // given
        Diary diary = Diary.builder()
            .location(Location.create(1L, "경기도 수원시 팔달구 팔달문로", "웨딩컨벤션", BigDecimal.valueOf(127.1255), BigDecimal.valueOf(90.6543), Category.ETC))
            .coupleId(1L)
            .boyText("우리도 곧 결혼하자")
            .girlText("식장 이뿌더랑")
            .score(5)
            .datingDay(LocalDate.of(2023, 10, 23))
            .photos(Photos.builder().firstImage("test-image").build())
            .build();

        Integer score = 4;
        LocalDate datingDay = LocalDate.of(2023, 11, 1);
        String category = "ACCOMODATION";
        String myText = "여기가 더 좋았어!";
        List<String> uploadedImageUrls = List.of("new-image1", "new-image2", "new-image3");

        // when
        diary.update(MALE, score, datingDay, category, myText, uploadedImageUrls);

        // then
        assertAll(
            () -> assertThat(diary.getScore()).isEqualTo(score),
            () -> assertThat(diary.getDatingDay()).isEqualTo(datingDay),
            () -> assertThat(diary.getLocation().getCategory()).isEqualTo(Category.valueOf(category)),
            () -> assertThat(diary.getBoyText()).isEqualTo(myText)
            );
    }

    @DisplayName("update를 통해 다이어리 관련된 값들을 업데이트 할 수 있다.")
    @Test
    void update_Girl() {
        // given
        Diary diary = Diary.builder()
            .location(Location.create(1L, "경기도 수원시 팔달구 팔달문로", "웨딩컨벤션", BigDecimal.valueOf(127.1255), BigDecimal.valueOf(90.6543), Category.ETC))
            .coupleId(1L)
            .boyText("우리도 곧 결혼하자")
            .girlText("식장 이뿌더랑")
            .score(5)
            .datingDay(LocalDate.of(2023, 10, 23))
            .photos(Photos.builder().firstImage("test-image").build())
            .build();

        Integer score = 4;
        LocalDate datingDay = LocalDate.of(2023, 11, 1);
        String category = "ACCOMODATION";
        String myText = "여기가 더 좋았어!";
        List<String> uploadedImageUrls = List.of("new-image1", "new-image2", "new-image3");

        // when
        diary.update(FEMALE, score, datingDay, category, myText, uploadedImageUrls);

        // then
        assertAll(
            () -> assertThat(diary.getScore()).isEqualTo(score),
            () -> assertThat(diary.getDatingDay()).isEqualTo(datingDay),
            () -> assertThat(diary.getLocation().getCategory()).isEqualTo(Category.valueOf(category)),
            () -> assertThat(diary.getGirlText()).isEqualTo(myText)
        );
    }

}
