package com.lovely4k.backend.authentication;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.CoupleCreatedEvent;
import com.lovely4k.backend.couple.CoupleUpdatedEvent;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.MemberupdatedEvent;
import com.lovely4k.backend.member.Role;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionInfoUpdateHandlerTest {

    @Mock
    HttpSession httpSession;

    @InjectMocks
    SessionInfoUpdateHandler sessionInfoUpdateHandler;


    @DisplayName("커플이 invitaion 코드가 생성되면 invitaion 코드를 발급한 유저의 세션 정보가 업데이트 된다.")
    @Test
    void test2() {

        //given
        Couple couple = Mockito.mock(Couple.class);
        given(couple.getId()).willReturn(1L);
        Member member = Member.builder().coupleId(1L).imageUrl("testUrl").nickname("nickName").role(Role.GUEST).coupleId(1L).build();

        CoupleCreatedEvent event = new CoupleCreatedEvent(couple);
        MyOAuth2Member principal = new MyOAuth2Member(Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())), "test", member);


        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
            principal, principal.getAuthorities(), principal.getNameAttributeKey()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        sessionInfoUpdateHandler.updateSessionAfterCoupleCreatedEvent(event);

        //then
        verify(httpSession).setAttribute(
            eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
            any(SecurityContext.class)
        );

        OAuth2AuthenticationToken updatedAuthentication =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertAll(
            () -> assertThat(updatedAuthentication).isNotNull(),
            () -> assertThat(updatedAuthentication.getPrincipal()).isInstanceOf(MyOAuth2Member.class),
            () -> assertThat(((MyOAuth2Member) updatedAuthentication.getPrincipal()).getCoupleId()).isEqualTo(member.getCoupleId())
        );
    }

    @DisplayName("커플이 맺어지면 invitaion 코드를 받은 유저의 세션 정보가 업데이트 된다")
    @Test
    void updateSessionAfterCoupleUpdatedEvent() {

        //given
        Couple couple = Mockito.mock(Couple.class);
        given(couple.getId()).willReturn(1L);
        Member member = Member.builder().coupleId(1L).imageUrl("testUrl").nickname("nickName").role(Role.GUEST).coupleId(1L).build();

        CoupleUpdatedEvent event = new CoupleUpdatedEvent(couple);
        MyOAuth2Member principal = new MyOAuth2Member(Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())), "test", member);


        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
            principal, principal.getAuthorities(), principal.getNameAttributeKey()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        sessionInfoUpdateHandler.updateSessionAfterCoupleUpdatedEvent(event);

        // then
        verify(httpSession).setAttribute(
            eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
            any(SecurityContext.class)
        );


        OAuth2AuthenticationToken updatedAuthentication =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertAll(
            () -> assertThat(updatedAuthentication).isNotNull(),
            () -> assertThat(updatedAuthentication.getPrincipal()).isInstanceOf(MyOAuth2Member.class),
            () -> assertThat(((MyOAuth2Member) updatedAuthentication.getPrincipal()).getCoupleId()).isEqualTo(member.getCoupleId())
        );
    }

    @DisplayName("사용자 정보가 바뀌면 로그아웃 없이 세션 정보가 업데이트 된다.")
    @Test
    void whenMemberUpdatedEvent_thenAuthenticationShouldBeUpdated() {
        // given
        Member member = Member.builder().coupleId(1L).imageUrl("testUrl").nickname("nickName").role(Role.GUEST).build();

        MemberupdatedEvent event = new MemberupdatedEvent(member);
        MyOAuth2Member principal = new MyOAuth2Member(Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())), "test", member);


        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
            principal, principal.getAuthorities(), principal.getNameAttributeKey()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        sessionInfoUpdateHandler.updateSessionAfterMemberUpdatedEvent(event);

        // then
        verify(httpSession).setAttribute(
            eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
            any(SecurityContext.class)
        );

        OAuth2AuthenticationToken updatedAuthentication =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertAll(
            () -> assertThat(updatedAuthentication).isNotNull(),
            () -> assertThat(updatedAuthentication.getPrincipal()).isInstanceOf(MyOAuth2Member.class),
            () -> assertThat(((MyOAuth2Member) updatedAuthentication.getPrincipal()).getCoupleId()).isEqualTo(member.getCoupleId())
        );
    }
}