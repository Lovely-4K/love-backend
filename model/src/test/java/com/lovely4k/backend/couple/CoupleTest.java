package com.lovely4k.backend.couple;

import com.lovely4k.backend.member.Sex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CoupleTest {

    @Test
    @DisplayName("여자친구를 등록한다.")
    void registerGirlId() throws Exception {
        //given
        Couple couple = Couple.builder()
                .boyId(1L)
                .invitationCode("sampleInvitationCode")
                .build();

        //when
        couple.registerGirlId(2L);

        //then
        assertThat(couple.getGirlId())
                .isEqualTo(2L);
    }

    @Test
    @DisplayName("남자친구를 등록한다.")
    void registerBoyId() throws Exception {
        //given
        Couple couple = Couple.builder()
                .girlId(1L)
                .invitationCode("sampleInvitationCode")
                .build();

        //when
        couple.registerBoyId(2L);

        //then
        assertThat(couple.getBoyId())
                .isEqualTo(2L);
    }

    @Test
    @DisplayName("커플 프로필을 수정한다.")
    void update() throws Exception {
        //given
        Couple couple = Couple.builder()
                .boyId(1L)
                .girlId(2L)
                .invitationCode("sampleInvitationCode")
                .build();

        LocalDate meetDay = LocalDate.of(2023, 10, 29);

        //when
        couple.update(meetDay);

        //then
        assertThat(couple.getMeetDay())
                .isEqualTo(meetDay);
    }

    @DisplayName("커플의 기본 온도는 0도이다. (남자가 커플을 만드는 경우)")
    @Test
    void couple_temperature_boy() {
        // given
        Long requestMemberId = 1L;
        Sex sex = Sex.MALE;
        String invitationCode = "test-invitation-code";

        // when
        Couple couple = Couple.create(requestMemberId, sex, invitationCode);

        // then
        assertAll(
                () -> assertThat(couple.getTemperature()).isEqualTo(0f),
                () -> assertThat(couple.getBoyId()).isNotNull()
        );
    }

    @DisplayName("커플의 기본 온도는 0도이다. (여자가 커플을 만드는 경우)")
    @Test
    void couple_temperature_girl() {
        // given
        Long requestMemberId = 1L;
        Sex sex = Sex.FEMALE;
        String invitationCode = "test-invitation-code";

        // when
        Couple couple = Couple.create(requestMemberId, sex, invitationCode);

        // then
        assertAll(
                () -> assertThat(couple.getTemperature()).isEqualTo(0f),
                () -> assertThat(couple.getGirlId()).isNotNull()
        );
    }

    @DisplayName("increaseTemperature를 통해 커플의 온도를 올릴 수 있다.")
    @Test
    void increaseTemperature() {
        // given
        Couple couple = Couple.builder()
                .boyId(1L)
                .girlId(2L)
                .meetDay(LocalDate.of(2020, 10, 20))
                .invitationCode("test-invitation-code")
                .temperature(0f)
                .build();

        assertThat(couple.getTemperature()).isEqualTo(0f);
        // when
        couple.increaseTemperature();

        // then
        assertThat(couple.getTemperature()).isEqualTo(1f);
    }

    @DisplayName("커플의 온도는 최대 100도 이다.")
    @Test
    void increaseTemperature_max100() {
        // given
        Couple couple = Couple.builder()
                .boyId(1L)
                .girlId(2L)
                .meetDay(LocalDate.of(2020, 10, 20))
                .invitationCode("test-invitation-code")
                .temperature(100f)
                .build();
        assertThat(couple.getTemperature()).isEqualTo(100f);
        // when
        couple.increaseTemperature();

        // then
        assertThat(couple.getTemperature()).isEqualTo(100f);
    }

}
