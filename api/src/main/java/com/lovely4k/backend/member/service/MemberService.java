package com.lovely4k.backend.member.service;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberProfileGetResponse getMemberProfile(Long memberId) {

        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id 입니다."));

        return MemberProfileGetResponse.of(findMember);
    }
}
