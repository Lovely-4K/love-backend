package com.lovely4k.backend.member.service;

import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 정보를 조회한다.")
    void getMemberProfile() throws Exception {
        //given
        Member member = createMember();

        Member savedMember = memberRepository.save(member);

        //when
        MemberProfileGetResponse memberProfileGetResponse = memberService.getMemberProfile(savedMember.getId());

        //then
        Assertions.assertThat(memberProfileGetResponse)
            .extracting("sex", "name")
            .contains("boy", "김철수");
    }

    @Test
    @DisplayName("존재하지 않는 id로 프로필을 조회할 경우 예외가 발생한다.")
    void getMemberProfileByWrongId() throws Exception {
        //given
        Member member = createMember();

        memberRepository.save(member);

        //when && then
        Assertions.assertThatThrownBy(() -> memberService.getMemberProfile(100L))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private Member createMember() {
        return Member.builder()
            .coupleId(1L)
            .sex("boy")
            .name("김철수")
            .nickname("듬직이")
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti("ESFJ")
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }


}