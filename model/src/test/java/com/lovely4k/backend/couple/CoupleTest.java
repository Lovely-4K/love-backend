package com.lovely4k.backend.couple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
}
