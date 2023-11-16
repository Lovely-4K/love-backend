package com.lovely4k.backend.authentication;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${love.service.redirect-url}")
    private String redirectUrl;

    private final CoupleRepository coupleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        MyOAuth2Member oAuth2Member = (MyOAuth2Member) authentication.getPrincipal();
        Long coupleId = oAuth2Member.getCoupleId();

        Optional<Couple> optionalCouple = coupleRepository.findDeletedById(coupleId);
        optionalCouple.ifPresentOrElse(
            couple -> {
                if (couple.isRecoupleReceiver(oAuth2Member.getMemberId())) {
                    log.debug("send code!!");
                    sendRecoupleCode(response, coupleId);
                } else {
                    sendCode(response);
                }
            }
            , () -> sendCode(response)
        );

    }

    private void sendRecoupleCode(HttpServletResponse response, Long coupleId) {
        String recoupleUrl = redirectUrl + "/recouple/" + coupleId;
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", redirectUrl);
        response.setHeader("recouple-url", recoupleUrl);
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong while generating response message", e);
        }
    }

    private void sendCode(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", redirectUrl);
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong while generating response message", e);
        }
    }

}
