package com.lovely4k.backend.member;

import org.junit.jupiter.api.Assertions;
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
        Member member = createMemberWithoutCoupleId(LocalDate.of(1996, 7, 30), "ESFJ", "#FFFFFF");

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
    void registerProfileInfoWithInfo() {
        //given
        Member member = createMemberWithoutCoupleId(LocalDate.of(1996, 7, 30), "ESFJ", "#FFFFFF");

        //when
        member.registerProfileInfo(1L);

        //then
        assertThat(member.getCoupleId())
            .isEqualTo(1L);
    }

    @Test
    @DisplayName("회원 정보를 입력하지 않은 회원이 커플등록 할 경우 birthday, mbti, calendarColor가 임의로 입력되고 Role이 USER로 변경된다.")
    void registerProfileInfoWithoutInfo() {
        //given
        Member member = createMemberWithoutCoupleId(null, null, null);

        //when
        member.registerProfileInfo(1L);

        //then
        Assertions.assertAll(
            () -> assertThat(member.getBirthday()).isNotNull(),
            () -> assertThat(member.getMbti()).isNotNull(),
            () -> assertThat(member.getCalendarColor()).isNotNull(),
            () -> assertThat(member.getRole()).isEqualTo(Role.USER)
        );
    }


    private Member createMemberWithoutCoupleId(LocalDate birthday, String esfj, String hashtag) {
        return Member.builder()
            .sex(MALE)
            .nickname("듬직이")
            .birthday(birthday)
            .mbti(esfj)
            .calendarColor(hashtag)
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }

}
