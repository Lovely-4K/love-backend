package com.lovely4k.backend.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("회원의 프로필을 수정한다.")
    void updateProfile() throws Exception {
        //given
        Member member = createMemberWithoutCoupleId();

        //when
        member.updateProfile(
            "sampleImageUrl",
            "깜찍이",
            LocalDate.of(1997, 2, 3),
            "INFP",
            "#FFFFFF");

        //then
        assertThat(member).extracting("nickname", "calendarColor")
            .contains("깜찍이", "#FFFFFF");
    }

    @Test
    @DisplayName("커플 id를 등록한다.")
    void registerCoupleId() throws Exception {
        //given
        Member member = createMemberWithoutCoupleId();

        //when
        member.registerCoupleId(1L);

        //then
        assertThat(member.getCoupleId())
            .isEqualTo(1L);
    }


    private Member createMemberWithoutCoupleId() {
        return Member.builder()
            .sex(MALE)
            .nickname("듬직이")
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti("ESFJ")
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }

}
