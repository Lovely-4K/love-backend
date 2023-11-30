package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.common.imageuploader.ImageUploader;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.Photos;
import com.lovely4k.backend.diary.controller.request.DiaryDeleteRequest;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.request.DiaryEditRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        savedMember.registerProfileInfo(savedCouple.getId());
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

    @DisplayName("이미지가 없는 경우 이미지가 없는 채로 다이어리가 생성된다.")
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
        assertThat(findDiary.getPhotos()).isNull();
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

        DiaryEditRequest diaryEditRequest = new DiaryEditRequest(4, LocalDate.of(2023, 11, 1), "food", "boy-text",  List.of());

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
            () -> assertThat(findDiary.getBoyText()).isEqualTo(diaryEditRequest.text()),
            () -> assertThat(findDiary.getPhotos().countOfImages()).isEqualTo(2)
        );
    }

    @Transactional
    @DisplayName("다이어리 수정 시 images 에 값이 존재하는 이미지의 경우 이미지가 유지된다.")
    @Test
    void editDiary_images() {
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

        DiaryEditRequest diaryEditRequest = new DiaryEditRequest(4, LocalDate.of(2023, 11, 1), "food", "boy-text",  List.of("test-image"));

        // stubbing
        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("first-image-url", "second-image-url"));

        // when
        diaryService.editDiary(savedDiary.getId(), List.of(firstImage, secondImage), diaryEditRequest, couple.getId(), 1L);

        // then
        Diary findDiary = diaryRepository.findById(savedDiary.getId()).orElseThrow();
        assertAll(
            () -> assertThat(findDiary.getScore()).isEqualTo(diaryEditRequest.score()),
            () -> assertThat(findDiary.getDatingDay()).isEqualTo(diaryEditRequest.datingDay()),
            () -> assertThat(findDiary.getLocation().getCategory()).isEqualTo(Category.FOOD),
            () -> assertThat(findDiary.getBoyText()).isEqualTo(diaryEditRequest.text()),
            () -> assertThat(findDiary.getPhotos().countOfImages()).isEqualTo(3)
        );
    }

    @Transactional
    @DisplayName("다이어리 수정 시 기존에 존재하는 이미지와 업로드하는 이미지를 합한 수가 5개를 넘을 경우 IllegalArgumentException이 발생한다.")
    @Test
    void editDiary_over5() {
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
            .photos(Photos.builder().firstImage("test-image").secondImage("test-image2").thirdImage("test-image3").fourthImage("test-image4").build())
            .build();
        Diary savedDiary = diaryRepository.save(diary);

        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        DiaryEditRequest diaryEditRequest = new DiaryEditRequest(4, LocalDate.of(2023, 11, 1), "food", "boy-text",  List.of("test-image", "test-image2", "test-image3", "test-image4"));

        // stubbing
        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("first-image-url", "second-image-url"));

        Long diaryId = savedDiary.getId();
        List<MultipartFile> imageList = List.of(firstImage, secondImage);
        Long coupleId = couple.getId();

        // when && then
        assertThatThrownBy(
            () ->diaryService.editDiary(diaryId, imageList, diaryEditRequest, coupleId, 1L)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미지는 최대 5개를 넘길 수 없습니다.")
        ;

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

        assertThat(diaries).hasSize(3);
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

    private static Member buildMember() {
        return Member.builder()
            .sex(MALE)
            .coupleId(1L)
            .nickname("Tommy")
            .role(Role.USER)
            .build();
    }
}