package com.lovely4k.backend.authentication.trial_login;

import com.lovely4k.backend.authentication.token.TokenDto;
import com.lovely4k.backend.authentication.token.TokenProvider;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public LoginResponse trialLogin(long memberId) {
        Member trialMember = memberRepository.findById(memberId).orElseThrow();
        TokenDto tokenDto = tokenProvider.generateTokenDto(trialMember);
        return LoginResponse.of(tokenDto);
    }
}
