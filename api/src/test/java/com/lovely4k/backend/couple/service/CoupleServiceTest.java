package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
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
    void registerCoupleWithWrongCode()  {
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
    void getCoupleProfile()  {
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

    @DisplayName("삭제 권한이 없는 경우 IllegalArgumentException이 발생한다.")
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
                .hasMessage(String.format("%s %d은 %s %d에 대한 권한이 없음", "member", 3, "couple", couple.getId()));
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