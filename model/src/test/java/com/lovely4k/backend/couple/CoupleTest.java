package com.lovely4k.backend.couple;

import com.lovely4k.backend.member.Sex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        couple.registerPartnerId(2L);

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
        couple.registerPartnerId(2L);

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

    @DisplayName("hasAuthority를 통해 couple에 대한 권한이 있는 지 검증 할 수 있다.")
    @CsvSource(value = {"1,true", "2,true", "3,false"})
    @ParameterizedTest
    void hasAuthority(Long memberId, boolean expected) {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .invitationCode("sampleInvitationCode")
            .build();

        // when
        boolean result = couple.hasAuthority(memberId);

        // then
        assertThat(result).isEqualTo(expected);
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

    @DisplayName("30일을 초과했을 경우 isExpired의 결과값은 true")
    @Test
    void isExpired_true() {
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("sampleInvitationCode")
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 7, 1))
            .build();

        // when
        boolean result = couple.isExpired(LocalDate.of(2022, 8, 1));

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("커플의 온도는 최대 100도 이다.")
    @Test
    void increaseTemperature_max100() {
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

    @DisplayName("30일을 초과하지 않았을 경우 isExpired의 결과값은 false")
    @Test
    void isExpired_false() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .invitationCode("sampleInvitationCode")
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 7, 1))
            .build();

        // when
        boolean result = couple.isExpired(LocalDate.of(2022, 7, 31));

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("관계가 깨진 커플에 대해서 recouple method를 통해 재결합 신청을 할 수 있다.")
    @Test
    void recouple_request() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .coupleStatus(CoupleStatus.BREAKUP)
            .build();

        // when
        couple.recouple(1L, LocalDate.of(2022, 10, 30));

        // then
        assertAll(
            () -> assertThat(couple.getCoupleStatus()).isEqualTo(CoupleStatus.RECOUPLE),
            () -> assertThat(couple.getReCoupleRequesterId()).isEqualTo(1L)
        );
    }

    @DisplayName("Recouple 상태에 있는 커플이 재결합을 수락한다면, 재결합이 성공한다.")
    @Test
    void recouple_receive() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .coupleStatus(CoupleStatus.RECOUPLE)
            .reCoupleRequesterId(1L)
            .build();

        // when
        couple.recouple(2L, LocalDate.of(2022, 10, 30));

        // then
        assertAll(
            () -> assertThat(couple.getCoupleStatus()).isEqualTo(CoupleStatus.RELATIONSHIP),
            () -> assertThat(couple.getReCoupleRequesterId()).isNull(),
            () -> assertThat(couple.getDeletedDate()).isNull(),
            () -> assertThat(couple.isDeleted()).isFalse()
        );
    }

    @DisplayName("RELATIONSHIP 관계에 있는 커플의 경우 recouple 을 할 수 없다.")
    @Test
    void recouple_invalid_couple_status() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .coupleStatus(CoupleStatus.RELATIONSHIP)
            .build();

        // when && then
        LocalDate requestedDate = LocalDate.of(2022, 10, 30);

        assertThatThrownBy(
            () -> couple.recouple(1L, requestedDate)
        ).isInstanceOf(IllegalStateException.class)
            .hasMessage("현재 커플의 경우 재결합을 할 수 있는 상태가 아닙니다.");
    }

    @DisplayName("자신의 커플이 아닌 커플에 대해서는 recouple을 할 수 없다.")
    @Test
    void recouple_noAuthority() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .coupleStatus(CoupleStatus.RECOUPLE)
            .reCoupleRequesterId(1L)
            .build();

        // when && then
        LocalDate requestedDate = LocalDate.of(2022, 10, 30);

        assertThatThrownBy(
            () -> couple.recouple(3L, requestedDate)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("관계가 깨진지 30일이 지난 커플의 경우 recouple을 할 수 없다.")
    @Test
    void recouple_expired() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .coupleStatus(CoupleStatus.BREAKUP)
            .build();

        // when && then
        LocalDate requestedDate = LocalDate.of(2023, 10, 30);

        assertThatThrownBy(
            () -> couple.recouple(2L, requestedDate)
        ).isInstanceOf(IllegalStateException.class)
            .hasMessage("커플을 끊은 지 30일이 지났기 때문에 복원을 할 수 없습니다.");
    }

    @DisplayName("재결합 요청자가 재결합 승인을 하려고 하는 경우 예외가 발생한다.")
    @Test
    void recouple_invalid_access() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .coupleStatus(CoupleStatus.RECOUPLE)
            .reCoupleRequesterId(1L)
            .build();

        LocalDate requestedDate = LocalDate.of(2022, 10, 30);
        // when && then
        assertThatThrownBy(
            () -> {
                couple.recouple(1L, requestedDate);
            }
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재결합 신청한 요청자는 재결합을 수락할 수 없습니다.");
    }

    @DisplayName("getOpponentId를 통해 커플 상대방의 id를 조회할 수 있다.")
    @Test
    void getOpponentId() {
        // given
        Couple couple = Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .temperature(40.0f)
            .coupleStatus(CoupleStatus.RELATIONSHIP)
            .build();

        // when
        Long opponentId = couple.getOpponentId(1L);

        // then
        assertThat(opponentId).isEqualTo(2L);
    }
}
