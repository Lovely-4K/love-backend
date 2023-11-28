package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.response.FindCoupleProfileResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class CoupleQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    CoupleQueryRepository coupleQueryRepository;

    @Autowired
    CoupleRepository coupleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("커플이 등록되지 않은 경우 나의 정보만 조회할 수 있다.")
    void findCoupleProfileAlone() throws Exception {
        //given
        Member member = createMember(Sex.MALE, "ESFJ", "듬직이");
        Member savedMember = memberRepository.save(member);
        Couple createdCouple = createCoupleByMale(savedMember.getId(), "sampleCode");
        coupleRepository.save(createdCouple);


        //when
        FindCoupleProfileResponse response = coupleQueryRepository.findCoupleProfile(savedMember.getId());

        //then
        assertThat(response).extracting("myNickname", "myMbti", "opponentNickname", "opponentMbti")
            .containsExactly("듬직이", "ESFJ", null, null);
    }

    @Test
    @DisplayName("커플이 등록된 경우 나와 상대방의 정보를 함께 조회할 수 있다.")
    void findCoupleProfileTogether() {
        //given
        Member boyfriend = createMember(Sex.MALE, "ESFJ", "듬직이");
        Member girlfriend = createMember(Sex.FEMALE, "INFP", "깜찍이");
        Member savedBoyFriend = memberRepository.save(boyfriend);
        Member savedGirlFriend = memberRepository.save(girlfriend);

        Couple createdCouple = createCoupleByMale(savedBoyFriend.getId(), "sampleCode");
        createdCouple.registerPartnerId(savedGirlFriend.getId());
        coupleRepository.save(createdCouple);

        savedBoyFriend.registerProfileInfo(createdCouple.getId());
        savedGirlFriend.registerProfileInfo(createdCouple.getId());

        //when
        FindCoupleProfileResponse response = coupleQueryRepository.findCoupleProfile(savedBoyFriend.getId());

        //then
        assertThat(response).extracting("myNickname", "myMbti", "opponentNickname", "opponentMbti")
            .containsExactly("듬직이", "ESFJ", "깜찍이", "INFP");
    }

    private Member createMember(Sex sex, String mbti, String nickname) {
        return Member.builder()
            .sex(sex)
            .nickname(nickname)
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti(mbti)
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .ageRange("10-20")
            .role(Role.USER)
            .build();
    }

    private Couple createCoupleByMale(Long requestedMemberId, String invitationCode) {
        return Couple.builder()
            .boyId(requestedMemberId)
            .girlId(null)
            .meetDay(null)
            .invitationCode(invitationCode)
            .temperature(0.0f)
            .build();
    }
}