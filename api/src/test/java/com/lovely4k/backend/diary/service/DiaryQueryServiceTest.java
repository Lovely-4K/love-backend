package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.Photos;
import com.lovely4k.backend.diary.service.response.WebDiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryListByMarkerResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryListResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryMarkerResponse;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.location.LocationRepository;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.repository.MemberRepository;
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
    LocationRepository locationRepository;

    @Autowired
    DiaryQueryService diaryQueryService;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        diaryRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
    }


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

    @DisplayName("findDiaryList 메서드를 통해 다이어리 목록을 조회해 올 수 있다. ")
    @Test
    void findDiaryList() {
        // given
        Diary food1 = buildDiary(Category.FOOD, 1L);
        Diary food2 = buildDiary(Category.FOOD, 1L);
        Diary accomodation1 = buildDiary(Category.ACCOMODATION, 1L);
        Diary accomodation2 = buildDiary(Category.ACCOMODATION, 2L);
        diaryRepository.saveAll(List.of(food1, food2, accomodation1, accomodation2));

        // when
        Page<WebDiaryListResponse> diaryList =
            diaryQueryService.findDiaryList(1L, Category.ACCOMODATION, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")));

        // then
        assertAll(
            () -> assertThat(diaryList.getNumberOfElements()).isEqualTo(1),
            () -> assertThat(diaryList.getTotalPages()).isEqualTo(1)
        );
    }

    @DisplayName("findDiaryList 메서드 실행 시 diary가 존재하지 않으면 빈 페이지를 반환한다.")
    @Test
    void findDiaryListNoDiary() {
        // when
        Page<WebDiaryListResponse> diaryList =
            diaryQueryService.findDiaryList(1L, Category.ACCOMODATION, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")));

        // then
        assertAll(
            () -> assertThat(diaryList.getNumberOfElements()).isZero(),
            () -> assertThat(diaryList.getTotalPages()).isEqualTo(1)
        );
    }

    @DisplayName("findDiaryListByMarker를 통해서 특정 kakaoMap에 해당하는 다이어리들을 조회 할 수 있다.")
    @Test
    void findDiaryListByMarker() {
        // given
        Diary food1 = buildDiaryWithLocationId(Category.FOOD, 1L, 1L);
        Diary food2 = buildDiaryWithLocationId(Category.FOOD, 1L, 1L);
        Diary accomodation1 = buildDiaryWithLocationId(Category.ACCOMODATION, 1L, 2L);
        Diary accomodation2 = buildDiaryWithLocationId(Category.ACCOMODATION, 2L, 2L);
        diaryRepository.saveAll(List.of(food1, food2, accomodation1, accomodation2));

        WebDiaryMarkerResponse webDiaryMarkerResponse1 = new WebDiaryMarkerResponse(food1.getId(), food1.getPhotos().getFirstImage(), food1.getDatingDay());
        WebDiaryMarkerResponse webDiaryMarkerResponse2 = new WebDiaryMarkerResponse(food2.getId(), food2.getPhotos().getFirstImage(), food2.getDatingDay());

        // when
        WebDiaryListByMarkerResponse webDiaryListByMarkerResponse = diaryQueryService.findDiaryListByMarker(1L, 1L);

        // then
        assertThat(webDiaryListByMarkerResponse.diaries()).containsAll(List.of(webDiaryMarkerResponse1, webDiaryMarkerResponse2));

    }

    @DisplayName("diary가 존재하지 않을 경우 findDiaryListByMarker의 결과는 null 이다.")
    @Test
    void findDiaryListByMarkerEmpty() {
        // when
        WebDiaryListByMarkerResponse webDiaryListByMarkerResponse = diaryQueryService.findDiaryListByMarker(1L, 1L);

        // then
        assertThat(webDiaryListByMarkerResponse).isNull();
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