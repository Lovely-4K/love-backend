package com.lovely4k.backend.member.service;

import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.request.MemberProfileEditServiceRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        MemberProfileGetResponse memberProfileGetResponse = memberService.findMemberProfile(savedMember.getId());

        //then
        assertThat(memberProfileGetResponse)
            .extracting("imageUrl", "name")
            .contains("http://www.imageUrlSample.com", "김철수");
    }

    @Test
    @DisplayName("존재하지 않는 id로 프로필을 조회할 경우 예외가 발생한다.")
    void getMemberProfileByWrongId() throws Exception {
        //given
        Member member = createMember();

        memberRepository.save(member);

        //when && then
        assertThatThrownBy(() -> memberService.findMemberProfile(100L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("회원의 프로필 정보를 수정할 수 있다.")
    void updateMemberProfile() throws Exception {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        MemberProfileEditServiceRequest serviceRequest = new MemberProfileEditServiceRequest(
            "http://www.imageUrlSample.com",
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when
        memberService.updateMemberProfile(serviceRequest, savedMember.getId());

        //then
        Member updatedMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 id 입니다."));

        assertThat(updatedMember).extracting("name", "nickname", "birthday")
            .contains("김동수", "길쭉이", LocalDate.of(1996, 7, 31));
    }

    private Member createMember() {
        return Member.builder()
            .coupleId(1L)
            .sex(MALE)
            .name("김철수")
            .nickname("듬직이")
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti("ESFJ")
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }
}
