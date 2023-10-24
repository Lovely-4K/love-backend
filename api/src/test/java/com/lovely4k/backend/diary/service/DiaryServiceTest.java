package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.location.LocationRepository;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class DiaryServiceTest extends IntegrationTestSupport {

    @Autowired
    DiaryService diaryService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    LocationRepository locationRepository;

    @DisplayName("createDiary 메서드를 통해 다이어리를 생성할 수 있다.")
    @Test
    void createDiary() {
        // given
        Member member = Member.builder()
                .name("tommy")
                .sex("boy")
                .build();
        memberRepository.save(member);

        DiaryCreateRequest diaryCreateRequest =
                new DiaryCreateRequest(1L, "경기도 일산", 4, LocalDate.of(2023, 10, 20), "ACCOMODATION", "테스트 다이어리");

        // when
        Long diaryId = diaryService.createDiary(diaryCreateRequest, member.getId());

        // then
        Diary findDiary = diaryRepository.findById(diaryId).orElseThrow();
        Location findLocation = locationRepository.findById(findDiary.getLocation().getId()).orElseThrow();

        assertAll(
                () -> assertThat(findDiary.getId()).isEqualTo(diaryId),
                () -> assertThat(findDiary.getBoyText()).isNotNull().isEqualTo("테스트 다이어리"),
                () -> assertThat(findLocation.getKakaoMapId()).isEqualTo(1L)
        );

    }

    @DisplayName("유효하지 않은 Member Id로 다이어리 생성하려고 할 경우 IllegalArgumentException이 발생한다.")
    @Test
    void createDiaryInvalidMemberId() {
        // given
        Member member = Member.builder()
                .name("tommy")
                .sex("boy")
                .build();
        memberRepository.save(member);
        Long invalidMemberId = member.getId() + 1;

        DiaryCreateRequest diaryCreateRequest =
                new DiaryCreateRequest(1L, "경기도 일산", 4, LocalDate.of(2023, 10, 20), "ACCOMODATION", "테스트 다이어리");

        // when && then
        assertThatThrownBy(
                () -> diaryService.createDiary(diaryCreateRequest, invalidMemberId)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid member id");
    }
}
