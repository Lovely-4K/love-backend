package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.request.CoupleProfileEditServiceRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    @DisplayName("초대 코드를 발급받을 수 있다.")
    void createInvitationCode() throws Exception {
        //given
        Member savedMember = memberRepository.save(createMember("MALE", "김철수", "ESFJ", "듬직이"));

        //when
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedMember.getId());

        //then
        Couple findCouple = coupleRepository.findById(response.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id입니다."));

        assertAll(
            () -> assertThat(findCouple.getId()).isNotNull(),
            () -> assertThat(findCouple.getInvitationCode()).isNotNull()
        );
    }

    @Test
    @DisplayName("초대코드를 통해 커플을 등록할 수 있다.")
    void registerCouple() throws Exception {
        //given
        Member requestedMember = createMember("MALE", "김철수", "ESFJ", "듬직이");
        Member receivedMember = createMember("FEMALE", "김영희", "ESFJ", "듬직이");

        Member savedRequestedMember = memberRepository.save(requestedMember);
        Member savedReceivedMember = memberRepository.save(receivedMember);

        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedRequestedMember.getId());

        //when
        coupleService.registerCouple(response.invitationCode(), savedReceivedMember.getId());

        //then
        Couple couple = coupleRepository.findById(response.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));

        assertAll(
            () -> assertThat(couple.getBoyId()).isNotNull(),
            () -> assertThat(couple.getGirlId()).isNotNull()
        );
    }

    @Test
    @DisplayName("잘못된 초대코드를 입력하면 예외가 발생한다.")
    void registerCoupleWithWrongCode() throws Exception {
        //given
        Member requestedMember = createMember("MALE", "김철수", "ESFJ", "듬직이");
        Member receivedMember = createMember("FEMALE", "김영희", "ESFJ", "듬직이");

        Member savedRequestedMember = memberRepository.save(requestedMember);
        Member savedReceivedMember = memberRepository.save(receivedMember);

        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedRequestedMember.getId());

        Long savedReceivedMemberId = savedReceivedMember.getId();

        //when && then
        assertThatThrownBy(() -> coupleService.registerCouple("wrongCode", savedReceivedMemberId))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("coupleId로 커플 프로필을 조회할 수 있다.")
    void getCoupleProfile() throws Exception {
        //given
        Member boy = createMember("MALE", "김철수", "ESTJ", "듬직이");
        Member girl = createMember("FEMALE", "김영희", "INFP", "깜찍이");
        memberRepository.saveAll(List.of(boy, girl));

        InvitationCodeCreateResponse codeCreateResponse = coupleService.createInvitationCode(boy.getId());

        coupleService.registerCouple(codeCreateResponse.invitationCode(), girl.getId());

        //when
        CoupleProfileGetResponse profileGetResponse = coupleService.getCoupleProfile(codeCreateResponse.coupleId());

        //then
        assertAll(
            () -> assertThat(profileGetResponse.boyMbti()).isEqualTo(boy.getMbti()),
            () -> assertThat(profileGetResponse.boyNickname()).isEqualTo(boy.getNickname()),
            () -> assertThat(profileGetResponse.girlMbti()).isEqualTo(girl.getMbti()),
            () -> assertThat(profileGetResponse.girlNickname()).isEqualTo(girl.getNickname())
        );
    }

    @Test
    @DisplayName("커플 프로필을 수정할 수 있다.")
    void updateCoupleProfile() throws Exception {
        //given
        CoupleProfileEditServiceRequest request = new CoupleProfileEditServiceRequest(LocalDate.of(2022, 7, 26));

        Member boy = createMember("MALE", "김철수", "ESTJ", "듬직이");
        Member girl = createMember("FEMALE", "김영희", "INFP", "깜찍이");
        memberRepository.saveAll(List.of(boy, girl));

        InvitationCodeCreateResponse codeCreateResponse = coupleService.createInvitationCode(boy.getId());

        coupleService.registerCouple(codeCreateResponse.invitationCode(), girl.getId());

        //when
        coupleService.updateCoupleProfile(request, boy.getId());

        //then
        Couple findCouple = coupleRepository.findById(codeCreateResponse.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));

        assertThat(findCouple.getMeetDay()).isEqualTo(request.meetDay());
    }

    private Member createMember(String sex, String name, String mbti, String nickname) {
        return Member.builder()
            .sex(sex)
            .name(name)
            .nickname(nickname)
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti(mbti)
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }
}
