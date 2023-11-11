package com.lovely4k.backend.authentication;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final CoupleRepository coupleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("onAuthenticationSuccess 요청 시작");
        MyOAuth2Member oAuth2Member = (MyOAuth2Member) authentication.getPrincipal();
        Long coupleId = oAuth2Member.getCoupleId();

        Optional<Couple> optionalCouple = coupleRepository.findDeletedById(coupleId);
        log.info("optionalCouple Exist? {}", optionalCouple.isPresent());
        optionalCouple.ifPresentOrElse(
            couple -> {
                if (couple.isRecoupleReceiver(oAuth2Member.getMemberId())) {
                    log.info("send code!!");
                    sendRecoupleCode(response, coupleId);
                }
            }
            , () -> {
            }
        );

    }

    private void sendRecoupleCode(HttpServletResponse response, Long coupleId) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON.getType());
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write("""
                recouple request is in progress. do you want to recouple? 
                if you want to recouple, request api below.
                [POST] http://localhost:8080/recouple/ """ + coupleId);
        } catch (IOException e) {
            throw new IllegalStateException("something went wrong while generating response message", e);
        }
    }
}
