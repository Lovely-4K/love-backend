package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.CoupleStatus;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.request.CoupleProfileEditServiceRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.lovely4k.backend.member.Sex.FEMALE;
import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@Transactional
class CoupleServiceTest extends IntegrationTestSupport {

    @Autowired
    private CoupleService coupleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoupleRepository coupleRepository;


    @Test
    @DisplayName("초대 코드를 발급받을 수 있다. - MALE 이 코드를 넘겨 줄 경우")
    void createInvitationCodeByMale() {
        //given
        Member savedMember = memberRepository.save(createMember(MALE, "ESFJ", "듬직이"));

        //when
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedMember.getId(), savedMember.getSex().name());

        //then
        Couple findCouple = coupleRepository.findById(response.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id입니다."));

        assertAll(
            () -> assertThat(findCouple.getId()).isNotNull(),
            () -> assertThat(findCouple.getInvitationCode()).isNotNull(),
            () -> assertThat(findCouple.getBoyId()).isEqualTo(savedMember.getId())
        );
    }

    @Test
    @DisplayName("초대 코드를 발급받을 수 있다. - FEMALE 이 코드를 넘겨 줄 경우")
    void createInvitationCodeByFemale() {
        //given
        Member savedMember = memberRepository.save(createMember(FEMALE, "INFP", "깜찍이"));

        //when
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedMember.getId(), savedMember.getSex().name());

        //then
        Couple findCouple = coupleRepository.findById(response.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id입니다."));

        assertAll(
            () -> assertThat(findCouple.getId()).isNotNull(),
            () -> assertThat(findCouple.getInvitationCode()).isNotNull(),
            () -> assertThat(findCouple.getGirlId()).isEqualTo(savedMember.getId())
        );
    }

    @Test
    @DisplayName("초대코드를 통해 커플을 등록할 수 있다.")
    void registerCouple() {
        //given
        Member requestedMember = createMember(MALE, "ESFJ", "듬직이");
        Member receivedMember = createMember(FEMALE, "ESFJ", "듬직이");

        Member savedRequestedMember = memberRepository.save(requestedMember);
        Member savedReceivedMember = memberRepository.save(receivedMember);

        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedRequestedMember.getId(), savedRequestedMember.getSex().name());

        //when
        coupleService.registerCouple(response.invitationCode(), savedReceivedMember.getId());

        //then
        Couple couple = coupleRepository.findById(response.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));

        assertAll(
            () -> assertThat(couple.getBoyId()).isEqualTo(savedRequestedMember.getId()),
            () -> assertThat(couple.getGirlId()).isEqualTo(savedReceivedMember.getId()),
            () -> assertThat(savedRequestedMember.getCoupleId()).isEqualTo(couple.getId()),
            () -> assertThat(savedReceivedMember.getCoupleId()).isEqualTo(couple.getId())
        );
    }

    @Test
    @DisplayName("잘못된 초대코드를 입력하면 예외가 발생한다.")
    void registerCoupleWithWrongCode() {
        //given
        Member requestedMember = createMember(MALE, "ESFJ", "듬직이");
        Member receivedMember = createMember(FEMALE, "ESFJ", "듬직이");

        Member savedRequestedMember = memberRepository.save(requestedMember);
        Member savedReceivedMember = memberRepository.save(receivedMember);


        Long savedReceivedMemberId = savedReceivedMember.getId();

        //when && then
        assertThatThrownBy(() -> coupleService.registerCouple("wrongCode", savedReceivedMemberId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("유효하지 않은 초대코드 입니다.");
    }

    @Test
    @DisplayName("memberId를 통하여 커플 프로필을 조회할 수 있다.")
    void getCoupleProfile() {
        //given
        Member boy = createMember(MALE, "ESTJ", "듬직이");
        Member girl = createMember(FEMALE, "INFP", "깜찍이");
        memberRepository.saveAll(List.of(boy, girl));

        InvitationCodeCreateResponse codeCreateResponse = coupleService.createInvitationCode(boy.getId(), boy.getSex().name());

        coupleService.registerCouple(codeCreateResponse.invitationCode(), girl.getId());

        //when
        CoupleProfileGetResponse profileGetResponse = coupleService.findCoupleProfile(boy.getId());

        //then
        assertAll(
            () -> assertThat(profileGetResponse.myMbti()).isEqualTo(boy.getMbti()),
            () -> assertThat(profileGetResponse.myNickname()).isEqualTo(boy.getNickname()),
            () -> assertThat(profileGetResponse.opponentMbti()).isEqualTo(girl.getMbti()),
            () -> assertThat(profileGetResponse.opponentNickname()).isEqualTo(girl.getNickname())
        );
    }

    @Test
    @DisplayName("커플 프로필을 수정할 수 있다.")
    void updateCoupleProfile() {
        //given
        CoupleProfileEditServiceRequest request = new CoupleProfileEditServiceRequest(LocalDate.of(2022, 7, 26));

        Member boy = createMember(MALE, "ESTJ", "듬직이");
        Member girl = createMember(FEMALE, "INFP", "깜찍이");
        memberRepository.saveAll(List.of(boy, girl));

        InvitationCodeCreateResponse codeCreateResponse = coupleService.createInvitationCode(boy.getId(), boy.getSex().name());

        coupleService.registerCouple(codeCreateResponse.invitationCode(), girl.getId());

        //when
        coupleService.updateCoupleProfile(request, boy.getId());

        //then
        Couple findCouple = coupleRepository.findById(codeCreateResponse.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));

        assertThat(findCouple.getMeetDay()).isEqualTo(request.meetDay());
    }

    @DisplayName("커플을 삭제할 경우 다이어리를 조회할 수 없어야 한다.")
    @Test
    void deleteCouple() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .build();
        Couple savedCouple = coupleRepository.save(couple);

        Couple findCouple = coupleRepository.findById(savedCouple.getId()).orElseThrow();
        assertAll(
            () -> assertThat(findCouple.isDeleted()).isFalse(),
            () -> assertThat(findCouple.getDeletedDate()).isNull()
        );

        // when
        coupleService.deleteCouple(savedCouple.getId(), 1L);

        // then
        Optional<Couple> optionalCouple = coupleRepository.findById(savedCouple.getId());
        assertThat(optionalCouple).isNotPresent();

    }

    @DisplayName("커플을 끊을 경우 deleted = true, deleted_date가 기록되며, Couple Status = 'BREAKUP' 이 된다.")
    @Test
    void deleteCouple_checkStatus() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .build();
        Couple savedCouple = coupleRepository.save(couple);

        Couple findCouple = coupleRepository.findById(savedCouple.getId()).orElseThrow();
        assertAll(
            () -> assertThat(findCouple.isDeleted()).isFalse(),
            () -> assertThat(findCouple.getDeletedDate()).isNull()
        );

        // when
        coupleService.deleteCouple(savedCouple.getId(), 1L);

        // then
        Couple deletedCouple = coupleRepository.findDeletedById(savedCouple.getId()).orElseThrow();
        assertAll(
            () -> assertThat(deletedCouple.isDeleted()).isTrue(),
            () -> assertThat(deletedCouple.getCoupleStatus()).isEqualTo(CoupleStatus.BREAKUP),
            () -> assertThat(deletedCouple.getDeletedDate()).isNotNull()
        );
    }

    @DisplayName("커플 끊기 권한이 없는 경우 IllegalArgumentException이 발생한다.")
    @Test
    void deleteCouple_noAuthority() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .build();
        Couple savedCouple = coupleRepository.save(couple);
        Long coupleId = savedCouple.getId();
        // when && then
        assertThatThrownBy(
            () -> coupleService.deleteCouple(coupleId, 3L)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("%s %d은 %s %d에 대한 권한이 없습니다.", "member", 3, "couple", couple.getId()));
    }

    @DisplayName("커플이 깨진 상태에서 recouple을 호출하면 재결합 신청을 할 수 있다.")
    @Test
    void recouple_request() {
        // given
        Member boy = createMember(MALE, "ESFJ", "tommy");
        Member girl1 = createMember(FEMALE, "ISFP", "lisa");
        memberRepository.saveAll(List.of(boy, girl1));

        // boy 와 girl1 연인 관계 생성
        InvitationCodeCreateResponse createResponse = coupleService.createInvitationCode(boy.getId(), "MALE");
        coupleService.registerCouple(createResponse.invitationCode(), girl1.getId());

        coupleRepository.flush();

        // boy 와 girl1 결별
        coupleService.deleteCouple(createResponse.coupleId(), boy.getId());

        // when
        coupleService.reCouple(LocalDate.of(2022, 10, 30), createResponse.coupleId(), boy.getId());

        // then
        Couple findCouple = coupleRepository.findDeletedById(createResponse.coupleId()).orElseThrow();

        assertAll(
            () -> assertThat(findCouple.getCoupleStatus()).isEqualTo(CoupleStatus.RECOUPLE),
            () -> assertThat(findCouple.getReCoupleRequesterId()).isEqualTo(boy.getId())
        );
    }

    @DisplayName("RECOUPLE 상태에서 recouple을 호출하면 원래의 관계로 돌아갈 수 있다.")
    @Test
    void recouple_approve() {
        // given
        Member boy = createMember(MALE, "ESFJ", "tommy");
        Member girl1 = createMember(FEMALE, "ISFP", "lisa");
        memberRepository.saveAll(List.of(boy, girl1));

        // boy 와 girl1 연인 관계 생성
        InvitationCodeCreateResponse createResponse = coupleService.createInvitationCode(boy.getId(), "MALE");
        coupleService.registerCouple(createResponse.invitationCode(), girl1.getId());

        coupleRepository.flush();

        // boy 와 girl1 결별
        coupleService.deleteCouple(createResponse.coupleId(), boy.getId());

        // boy가 recouple 신청
        coupleService.reCouple(LocalDate.of(2022, 10, 30), createResponse.coupleId(), boy.getId());


        // when
        coupleService.reCouple(LocalDate.of(2022, 10, 30), createResponse.coupleId(), girl1.getId());

        // then
        Couple findCouple = coupleRepository.findById(createResponse.coupleId()).orElseThrow();

        assertAll(
            () -> assertThat(findCouple.getCoupleStatus()).isEqualTo(CoupleStatus.RELATIONSHIP),
            () -> assertThat(findCouple.getReCoupleRequesterId()).isNull(),
            () -> assertThat(findCouple.getDeletedDate()).isNull(),
            () -> assertThat(findCouple.isDeleted()).isFalse()
        );
    }

    @DisplayName("recouple 요청 시 만약 상대방이 다른 사람과 커플을 맺은 상태라면, 재결합 신청을 할 수 없다.")
    @Test
    void recouple_alreadyCoupled() {
        // given
        Member boy = createMember(MALE, "ESFJ", "tommy");
        Member girl1 = createMember(FEMALE, "ISFP", "lisa");
        Member girl2 = createMember(FEMALE, "ENTJ", "elsa");

        memberRepository.saveAll(List.of(boy, girl1, girl2));

        // boy 와 girl1 연인 관계 생성
        InvitationCodeCreateResponse createResponse = coupleService.createInvitationCode(boy.getId(), "MALE");
        coupleService.registerCouple(createResponse.invitationCode(), girl1.getId());

        // boy 와 girl1 결별
        coupleService.deleteCouple(createResponse.coupleId(), boy.getId());

        // boy 와 girl2 연인 관계 생성
        InvitationCodeCreateResponse createResponse2 = coupleService.createInvitationCode(boy.getId(), "MALE");
        coupleService.registerCouple(createResponse2.invitationCode(), girl2.getId());

        // girl1 이 boy와 재결합을 원할 때 IllegalArgumentException 발생
        Member savedGirl1 = memberRepository.findById(girl1.getId()).orElseThrow();
        Couple deletedCouple = coupleRepository.findDeletedById(savedGirl1.getCoupleId()).orElseThrow();

        LocalDate requestedDate = deletedCouple.getDeletedDate().plusDays(5L);
        Long coupleId = savedGirl1.getCoupleId();
        Long memberId = savedGirl1.getId();

        assertThatThrownBy(
            () -> coupleService.reCouple(requestedDate, coupleId, memberId)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상대방은 커플 재결합을 할 수 있는 상태가 아닙니다.");

    }

    private Member createMember(Sex sex, String mbti, String nickname) {
        return Member.builder()
            .sex(sex)
            .nickname(nickname)
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti(mbti)
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .role(Role.USER)
            .build();
    }
}