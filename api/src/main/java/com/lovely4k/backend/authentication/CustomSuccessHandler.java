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
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            String jsonResponse = String.format("""
            {
                "code": 200,
                "message": "Recouple request is in progress. Do you want to recouple?",
                "recoupleUrl": "https://love-back.kro.kr/recouple/%d"
            }
            """, coupleId);
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong while generating response message", e);
        }
    }

    private void sendCode(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            String jsonResponse = """
            {
                "code": 200,
                "message": "Login success"
            }
            """;
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong while generating response message", e);
        }
    }

}
