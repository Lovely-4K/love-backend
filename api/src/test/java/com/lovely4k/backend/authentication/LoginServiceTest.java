package com.lovely4k.backend.authentication;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.authentication.trial_login.LoginResponse;
import com.lovely4k.backend.authentication.trial_login.LoginService;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class LoginServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    LoginService loginService;

    @DisplayName("trialLogin을 통해 accessToken 값과 refreshToken 값을 받아올 수 있다.")
    @Test
    void trialLogin() {

        // given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        // when
        LoginResponse loginResponse = loginService.trialLogin(savedMember.getId());

        // then
        RefreshToken refreshToken = refreshTokenRepository.findByMember(savedMember).orElseThrow();

        assertAll(
            () -> assertThat(loginResponse.accessToken()).isNotNull(),
            () -> assertThat(loginResponse.refreshToken()).isNotNull(),
            () -> assertThat(refreshToken).isNotNull(),
            () -> assertThat(refreshToken.getMember()).isEqualTo(savedMember)
        );

    }

    private Member createMember() {
        return Member.builder()
            .coupleId(1L)
            .sex(MALE)
            .nickname("듬직이")
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti("ESFJ")
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .role(Role.USER)
            .build();
    }
}