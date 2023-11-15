package com.lovely4k.backend.authentication;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.CoupleCreatedEvent;
import com.lovely4k.backend.couple.CoupleUpdatedEvent;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.MemberupdatedEvent;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class SessionInfoUpdateHandler {

    private final HttpSession httpSession;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateSessionAfterMemberUpdatedEvent(MemberupdatedEvent event) {
        Member member = event.member();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            MyOAuth2Member principal = (MyOAuth2Member) authentication.getPrincipal();
            OAuth2AuthenticationToken oAuth2AuthenticationToken =
                new OAuth2AuthenticationToken(
                    principal.update(
                        member.getCoupleId(),
                        member.getNickname(),
                        member.getImageUrl()
                    ),
                    authentication.getAuthorities(),
                    principal.getNameAttributeKey()
                );

            SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);

            SecurityContext securityContext = SecurityContextHolder.getContext();
            httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateSessionAfterCoupleCreatedEvent(CoupleCreatedEvent event) {
        Couple couple = event.couple();
        updateSessionWithCouple(couple);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateSessionAfterCoupleUpdatedEvent(CoupleUpdatedEvent event) {
        Couple couple = event.couple();
        updateSessionWithCouple(couple);
    }


    private void updateSessionWithCouple(Couple couple) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            MyOAuth2Member principal = (MyOAuth2Member) authentication.getPrincipal();
            OAuth2AuthenticationToken oAuth2AuthenticationToken =
                new OAuth2AuthenticationToken(
                    principal.update(
                        couple.getId(),
                        principal.getNickName(),
                        principal.getPicture()
                    ),
                    authentication.getAuthorities(),
                    principal.getNameAttributeKey()
                );

            SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);

            SecurityContext securityContext = SecurityContextHolder.getContext();
            httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        }

    }
}