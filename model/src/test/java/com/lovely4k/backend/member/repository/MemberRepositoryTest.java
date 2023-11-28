package com.lovely4k.backend.member.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MemberRepositoryTest extends IntegrationTestSupport {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void findCoupleMembersById() {
        //given
        Member member1 = createMember(Sex.MALE, "ESTJ", "듬직이");
        Member member2 = createMember(Sex.FEMALE, "INFP", "깜찍이");
        Member member3 = createMember(Sex.MALE, "INTP", "왕눈이");
        memberRepository.saveAll(List.of(member1, member2, member3));

        //when
        List<Member> coupleMembers = memberRepository.findCoupleMembersById(member1.getId(), member2.getId());

        //then
        assertThat(coupleMembers).hasSize(2)
            .extracting("mbti", "nickname")
            .contains(
                tuple("ESTJ", "듬직이"),
                tuple("INFP", "깜찍이")
            );


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
}