package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.service.response.WebDiaryDetailResponse;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.lovely4k.backend.member.Sex.FEMALE;
import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DiaryQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CoupleService coupleService;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DiaryQueryService diaryQueryService;


    @DisplayName("getDiaryDetail을 통해 다이어리 상세 정보를 조회할 수 있다.")
    @Test
    void getDiaryDetail() {
        // given

        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);

        Member member1 = Member.builder()
            .sex(MALE)
            .nickname("Tommy")
            .role(Role.USER)
            .build();

        Member member2 = Member.builder()
            .sex(FEMALE)
            .nickname("LIA")
            .role(Role.USER)
            .build();
        memberRepository.saveAll(List.of(member1, member2));

        InvitationCodeCreateResponse invitationCode = coupleService.createInvitationCode(member1.getId(), "MALE");
        coupleService.registerCouple(invitationCode.invitationCode(), member2.getId());


        Diary diary = Diary.builder()
            .coupleId(invitationCode.coupleId())
            .location(location)
            .boyText("hello")
            .girlText("hi")
            .score(4)
            .datingDay(LocalDate.of(2023, 10, 20))
            .build();

        diaryRepository.save(diary);

        Member boy = memberRepository.findById(member1.getId()).orElseThrow();

        // when
        WebDiaryDetailResponse webDiaryDetailResponse =
            diaryQueryService.findDiaryDetail(diary.getId(), boy.getCoupleId(), boy.getId());

        // then
        assertAll(
            () -> assertThat(webDiaryDetailResponse.kakaoMapId()).isEqualTo(10L),
            () -> assertThat(webDiaryDetailResponse.myText()).isEqualTo("hello"),
            () -> assertThat(webDiaryDetailResponse.opponentText()).isEqualTo("hi"),
            () -> assertThat(webDiaryDetailResponse.score()).isEqualTo(4)
        );
    }

    @DisplayName("다이어리 상세 정보 조회 시 잘못된 diary id인 경우 Null이 반환된다.")
    @Test
    void getDiaryDetailInvalidDiaryId() {
        // given

        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);
        Diary diary = buildDiary(location, 1L);
        diaryRepository.save(diary);

        Member member = buildMember();
        memberRepository.save(member);

        Long invalidDiaryId = diary.getId() + 1;
        Long coupleId = member.getCoupleId();
        Long memberId = member.getId();

        // when
        WebDiaryDetailResponse diaryDetail = diaryQueryService.findDiaryDetail(invalidDiaryId, coupleId, memberId);

        // then
        assertThat(diaryDetail).isNull();

    }

    @DisplayName("다이어리 상세 정보 조회 시 다른 커플이 작성한 다이어리 조회 시 Null 이다.")
    @Test
    void getDiaryDetailNoAuthority() {
        // given

        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);
        Diary diary = buildDiary(location, 2L);
        diaryRepository.save(diary);

        Member member = buildMember();
        memberRepository.save(member);

        Long diaryId = diary.getId();
        Long coupleId = member.getCoupleId();
        Long memberId = member.getId();

        // when
        WebDiaryDetailResponse diaryDetail = diaryQueryService.findDiaryDetail(diaryId, coupleId, memberId);

        // then
        assertThat(diaryDetail).isNull();
    }

    private static Diary buildDiary(Location location, long coupleId) {
        return Diary.builder()
            .coupleId(coupleId)
            .location(location)
            .boyText("hello")
            .girlText("hi")
            .score(4)
            .datingDay(LocalDate.of(2023, 10, 20))
            .build();
    }

    private static Diary buildDiary(Category category, long coupleId) {
        return Diary.builder()
            .coupleId(coupleId)
            .location(Location.create(1L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, category))
            .boyText("hello")
            .girlText("hi")
            .score(4)
            .datingDay(LocalDate.of(2023, 10, 20))
            .build();
    }

    private static Member buildMember() {
        return Member.builder()
            .sex(MALE)
            .coupleId(1L)
            .nickname("Tommy")
            .role(Role.USER)
            .build();
    }
}