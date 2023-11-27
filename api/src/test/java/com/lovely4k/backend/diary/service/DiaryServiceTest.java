package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.common.imageuploader.ImageUploader;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.Photos;
import com.lovely4k.backend.diary.controller.request.DiaryDeleteRequest;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.request.DiaryEditRequest;
import com.lovely4k.backend.diary.service.response.*;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.location.LocationRepository;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lovely4k.backend.member.Sex.FEMALE;
import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class DiaryServiceTest extends IntegrationTestSupport {

    @Autowired
    DiaryService diaryService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    CoupleRepository coupleRepository;

    @Autowired
    CoupleService coupleService;

    @MockBean
    ImageUploader imageUploader;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        diaryRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
    }

    @DisplayName("createDiary 메서드를 통해 다이어리를 생성할 수 있다.")
    @Test
    void createDiary() {
        // given
        Member member = buildMember();
        Member savedMember = memberRepository.save(member);

        Couple couple = Couple.create(savedMember.getId(), MALE, "test-code");
        Couple savedCouple = coupleRepository.save(couple);

        savedMember.registerCoupleId(savedCouple.getId());
        memberRepository.save(savedMember);

        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());
        List<MultipartFile> multipartFileList = List.of(firstImage, secondImage);

        DiaryCreateRequest diaryCreateRequest =
            new DiaryCreateRequest(1L, "경기도 일산", "starbucks", 4, LocalDate.of(2023, 10, 20), BigDecimal.ZERO, BigDecimal.ZERO, "ACCOMODATION", "테스트 다이어리");
        // stubbing
        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("first-image-url", "second-image-url"));

        // when
        Long diaryId = diaryService.createDiary(multipartFileList, diaryCreateRequest, member.getId());

        // then
        Diary findDiary = diaryRepository.findById(diaryId).orElseThrow();
        Location findLocation = locationRepository.findById(findDiary.getLocation().getId()).orElseThrow();

        assertAll(
            () -> assertThat(findDiary.getId()).isEqualTo(diaryId),
            () -> assertThat(findDiary.getBoyText()).isNotNull().isEqualTo("테스트 다이어리"),
            () -> assertThat(findLocation.getKakaoMapId()).isEqualTo(1L),
            () -> assertThat(findDiary.getPhotos()).isNotNull(),
            () -> assertThat(findDiary.getPhotos().getFirstImage()).isEqualTo("first-image-url"),
            () -> assertThat(findDiary.getPhotos().getSecondImage()).isEqualTo("second-image-url")
        );

    }

    @DisplayName("유효하지 않은 Member Id로 다이어리 생성하려고 할 경우 EntityNotFoundException이 발생한다.")
    @Test
    void createDiaryInvalidMemberId() {
        // given
        Member member = buildMember();
        Member savedMember = memberRepository.save(member);
        Long invalidMemberId = savedMember.getId() + 1;

        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());
        List<MultipartFile> multipartFileList = List.of(firstImage, secondImage);

        // stubbing
        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("first-image-url", "second-image-url"));

        DiaryCreateRequest diaryCreateRequest =
            new DiaryCreateRequest(1L, "경기도 일산", "starbucks", 4, LocalDate.of(2023, 10, 20), BigDecimal.ZERO, BigDecimal.ZERO, "ACCOMODATION", "테스트 다이어리");

        // when && then
        assertThatThrownBy(
            () -> diaryService.createDiary(multipartFileList, diaryCreateRequest, invalidMemberId)
        ).isInstanceOf(EntityNotFoundException.class)
            .hasMessage("invalid member id");
    }

    @DisplayName("이미지가 없는 경우 이미지업로드가 되지 않고, 서비스에서 제공하는 이미지로 채워진다.")
    @Test
    void createDiaryNoImage() throws InterruptedException {
        // given
        Member member = buildMember();
        memberRepository.save(member);

        Long memberId = member.getId();

        DiaryCreateRequest diaryCreateRequest =
            new DiaryCreateRequest(1L, "경기도 일산", "starbucks", 4, LocalDate.of(2023, 10, 20), BigDecimal.ZERO, BigDecimal.ZERO, "ACCOMODATION", "테스트 다이어리");

        // when
        Long savedDiaryId = diaryService.createDiary(Collections.emptyList(), diaryCreateRequest, memberId);

        // then
        Diary findDiary = diaryRepository.findById(savedDiaryId).orElseThrow();
        assertThat(findDiary.getPhotos()).isNotNull();
    }

    @DisplayName("5개 이상의 이미지를 업로드 하려고 하는 경우 IllegalArgumentException이 발생한다.")
    @Test
    void createDiaryTooMuchImage() {
        // given
        Member member = buildMember();
        memberRepository.save(member);
        Long memberId = member.getId();

        List<MultipartFile> multipartFileList = getMultipartFiles();

        // stubbing
        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("first-image-url", "second-image-url", "third-image-url", "fourth-image-url", "fifth-image-url", "sixth-image-url"));

        DiaryCreateRequest diaryCreateRequest =
            new DiaryCreateRequest(1L, "경기도 일산", "starbucks", 4, LocalDate.of(2023, 10, 20), BigDecimal.ZERO, BigDecimal.ZERO, "ACCOMODATION", "테스트 다이어리");
        // when && then
        assertThatThrownBy(
            () -> diaryService.createDiary(multipartFileList, diaryCreateRequest, memberId)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Image file can be uploaded maximum 5");
    }

    private static List<MultipartFile> getMultipartFiles() {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());
        MockMultipartFile thirdImage = new MockMultipartFile("images", "image3.png", "image/png", "some-image".getBytes());
        MockMultipartFile fourthImage = new MockMultipartFile("images", "image4.png", "image/png", "some-image".getBytes());
        MockMultipartFile fifthImage = new MockMultipartFile("images", "image5.png", "image/png", "some-image".getBytes());
        MockMultipartFile sixthImage = new MockMultipartFile("images", "image6.png", "image/png", "some-image".getBytes());
        return List.of(firstImage, secondImage, thirdImage, fourthImage, fifthImage, sixthImage);
    }

    @DisplayName("getDiaryDetail을 통해 다이어리 상세 정보를 조회할 수 있다.")
    @Test
    void getDiaryDetail() {
        // given

        Location location = Location.create(10L, "경기도 고양시", "starbucks",BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);

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
        DiaryDetailResponse diaryDetailResponse =
            diaryService.findDiaryDetail(diary.getId(), boy.getCoupleId(), boy.getId());

        // then
        assertAll(
            () -> assertThat(diaryDetailResponse.kakaoMapId()).isEqualTo(10L),
            () -> assertThat(diaryDetailResponse.myText()).isEqualTo("hello"),
            () -> assertThat(diaryDetailResponse.opponentText()).isEqualTo("hi"),
            () -> assertThat(diaryDetailResponse.score()).isEqualTo(4)
        );
    }

    @DisplayName("다이어리 상세 정보 조회 시 잘못된 diary id인 경우 EntityNotFoundException이 발생한다. ")
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

        // when && then
        assertThatThrownBy(
            () -> diaryService.findDiaryDetail(invalidDiaryId, coupleId, memberId)
        ).isInstanceOf(EntityNotFoundException.class)
            .hasMessage("invalid diary id");

    }

    @DisplayName("다이어리 상세 정보 조회 시 다른 커플이 작성한 다이어리는 조회할 수 없다. ")
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

        // when && then
        assertThatThrownBy(
            () -> diaryService.findDiaryDetail(diaryId, coupleId, memberId)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("you can only manage your couple's diary");
    }

    @Transactional
    @DisplayName("editDiary 메서드를 통해 다이어리를 수정할 수 있다.")
    @Test
    void editDiary() {
        // given

        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .build();
        coupleRepository.save(couple);
        Diary diary = Diary.builder()
            .location(Location.create(1L, "경기도 수원시 팔달구 팔달문로", "웨딩컨벤션", BigDecimal.valueOf(127.1255), BigDecimal.valueOf(90.6543), Category.ETC))
            .coupleId(couple.getId())
            .boyText("우리도 곧 결혼하자")
            .girlText("식장 이뿌더랑")
            .score(5)
            .datingDay(LocalDate.of(2023, 10, 23))
            .photos(Photos.builder().firstImage("test-image").build())
            .build();
        Diary savedDiary = diaryRepository.save(diary);

        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        DiaryEditRequest diaryEditRequest = new DiaryEditRequest(4, LocalDate.of(2023, 11, 1), "food", "boy-text", "girl-text");

        // stubbing
        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("first-image-url", "second-image-url"));

        // when
        diaryService.editDiary(savedDiary.getId(), List.of(firstImage, secondImage), diaryEditRequest, 1L, 1L);

        // then
        Diary findDiary = diaryRepository.findById(savedDiary.getId()).orElseThrow();
        assertAll(
            () -> assertThat(findDiary.getScore()).isEqualTo(diaryEditRequest.score()),
            () -> assertThat(findDiary.getDatingDay()).isEqualTo(diaryEditRequest.datingDay()),
            () -> assertThat(findDiary.getLocation().getCategory()).isEqualTo(Category.FOOD),
            () -> assertThat(findDiary.getBoyText()).isEqualTo(diaryEditRequest.myText()),
            () -> assertThat(findDiary.getGirlText()).isEqualTo(diaryEditRequest.opponentText()),
            () -> assertThat(findDiary.getPhotos().countOfImages()).isEqualTo(2)
        );
    }

    @DisplayName("deleteDiary 메서드를 통해 다이어리를 삭제할 수 있다. ")
    @Test
    void deleteDiary() {
        // given
        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);
        Diary diary = buildDiary(location, 1L);
        diaryRepository.save(diary);

        Member member = buildMember();
        memberRepository.save(member);

        Long diaryId = diary.getId();
        Long coupleId = member.getCoupleId();

        // when
        diaryService.deleteDiary(diaryId, coupleId);

        // then
        Optional<Diary> optionalDiary = diaryRepository.findById(diaryId);
        assertThat(optionalDiary).isEmpty();
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
        Page<DiaryListResponse> diaryList =
            diaryService.findDiaryList(1L, Category.ACCOMODATION, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")));

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
        Page<DiaryListResponse> diaryList =
            diaryService.findDiaryList(1L, Category.ACCOMODATION, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")));

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

        DiaryMarkerResponse diaryMarkerResponse1 = new DiaryMarkerResponse(food1.getId(), food1.getPhotos().getFirstImage(), food1.getDatingDay());
        DiaryMarkerResponse diaryMarkerResponse2 = new DiaryMarkerResponse(food2.getId(), food2.getPhotos().getFirstImage(), food2.getDatingDay());

        // when
        DiaryListByMarkerResponse diaryListByMarkerResponse = diaryService.findDiaryListByMarker(1L, 1L);

        // then
        assertThat(diaryListByMarkerResponse.diaries()).containsAll(List.of(diaryMarkerResponse1, diaryMarkerResponse2));

    }

    @DisplayName("diary가 존재하지 않을 경우 findDiaryListByMarker의 diaries는 empty 이다.")
    @Test
    void findDiaryListByMarkerEmpty() {
        // when
        DiaryListByMarkerResponse diaryListByMarkerResponse = diaryService.findDiaryListByMarker(1L, 1L);

        // then
        assertThat(diaryListByMarkerResponse.diaries()).isNull();
    }

    @DisplayName("findDiaryListInGrid를 통해 위치 기반 다이어리 목록을 조회할 수 있다.")
    @Test
    void findDiaryListInGrid() {
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
        DiaryListInGridResponse result = diaryService.findDiaryListInGrid(BigDecimal.valueOf(38), BigDecimal.valueOf(127), BigDecimal.valueOf(36), BigDecimal.valueOf(126), 1L);

        // then
        assertAll(
            () -> assertThat(result.diaries()).hasSize(1),
            () -> assertThat(result.diaries().get(0).diaryId()).isEqualTo(diary1.getId())
        );
    }

    @DisplayName("만족하는 결과가 없을경우 findDiaryListInGrid의 diaries는 Empty를 반환한다. ")
    @Test
    void findDiaryListInGrid_Empty() {
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
        DiaryListInGridResponse result = diaryService.findDiaryListInGrid(BigDecimal.valueOf(35), BigDecimal.valueOf(127), BigDecimal.valueOf(34), BigDecimal.valueOf(126), 1L);

        // then
        assertAll(
            () -> assertThat(result.diaries()).isEmpty()
        );
    }

    @DisplayName("다른 커플의 다이어리를 삭제할 수 없다.")
    @Test
    void deleteDiaryNoAuthority() {
        // given
        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);
        Diary diary = buildDiary(location, 2L);
        diaryRepository.save(diary);

        Member member = buildMember();
        memberRepository.save(member);

        Long diaryId = diary.getId();
        Long memberId = member.getId();

        // when && then
        assertThatThrownBy(
            () -> diaryService.deleteDiary(diaryId, memberId)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("you can only manage your couple's diary");

    }

    @DisplayName("동시에 여러가지의 다이어리를 삭제할 수 있다.")
    @Test
    void deleteDiaries() {
        // given
        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);
        Diary diary1 = buildDiary(location, 2L);
        Diary diary2 = buildDiary(location, 2L);
        Diary diary3 = buildDiary(location, 2L);
        diaryRepository.saveAll(List.of(diary1, diary2, diary3));

        DiaryDeleteRequest diaryDeleteRequest = new DiaryDeleteRequest(List.of(diary1.getId(), diary2.getId(), diary3.getId()));

        // when
        diaryService.deleteDiaries(diaryDeleteRequest, 2L);

        // then
        List<Diary> diaries = diaryRepository.findAll();

        assertThat(diaries).isEmpty();
    }

    @DisplayName("다이어리 삭제시 목록 중 하나라도 권한이 없다면 다이어리 모두 지워지지 않는다.")
    @Test
    void deleteDiaries_NoAuthority() {
        // given
        Location location = Location.create(10L, "경기도 고양시", "starbucks", BigDecimal.ZERO, BigDecimal.ZERO, Category.FOOD);
        Diary diary1 = buildDiary(location, 2L);
        Diary diary2 = buildDiary(location, 3L);
        Diary diary3 = buildDiary(location, 2L);
        diaryRepository.saveAll(List.of(diary1, diary2, diary3));

        DiaryDeleteRequest diaryDeleteRequest = new DiaryDeleteRequest(List.of(diary1.getId(), diary2.getId(), diary3.getId()));

        // when
        assertThatThrownBy(
            () -> diaryService.deleteDiaries(diaryDeleteRequest, 2L)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("you can only manage your couple's diary");


        // then
        List<Diary> diaries = diaryRepository.findAll();

        assertThat(diaries.size()).isEqualTo(3);
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

    private static Member buildMember() {
        return Member.builder()
            .sex(MALE)
            .coupleId(1L)
            .nickname("Tommy")
            .role(Role.USER)
            .build();
    }
}