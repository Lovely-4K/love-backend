package com.lovely4k.backend.diary.service.request;

import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DiaryCreateRequestTest {

    @DisplayName("toEntity 메서드를 활용해 Diary를 생성할 수 있다.")
    @Test
    void toEntity() {
        // given
        Member member = Member.builder()
                .nickname("tommy")
                .sex(MALE)
                .coupleId(1L)
                .build();

        DiaryCreateRequest diaryCreateRequest =
            new DiaryCreateRequest(1L, "경기도 고양시", "starbucks", 5, LocalDate.of(2023, 10, 20), "ACCOMODATION", "여기 되게 좋았어");

        // when
        Diary diary = diaryCreateRequest.toEntity(member);

        // then
        assertAll(
                () -> assertThat(diary.getBoyText()).isEqualTo("여기 되게 좋았어"),
                () -> assertThat(diary.getLocation().getKakaoMapId()).isEqualTo(1L),
                () -> assertThat(diary.getCoupleId()).isEqualTo(1L)
        );

    }
}
