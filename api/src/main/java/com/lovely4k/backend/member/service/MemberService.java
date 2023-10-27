package com.lovely4k.backend.member.service;

import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.request.MemberProfileEditServiceRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberProfileGetResponse findMemberProfile(Long memberId) {

        Member findMember = validateMemberId(memberId);

        return MemberProfileGetResponse.of(findMember);
    }

    // 프로필 사진 수정
    @Transactional
    public void updateMemberProfile(MemberProfileEditServiceRequest serviceRequest, Long memberId) {
        Member findMember = validateMemberId(memberId);

        updateMemberProfile(serviceRequest, findMember);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 id 입니다."));
    }

    private void updateMemberProfile(MemberProfileEditServiceRequest serviceRequest, Member findMember) {
        findMember.updateProfile(
            serviceRequest.sex(),
            serviceRequest.imageUrl(),
            serviceRequest.name(),
            serviceRequest.nickname(),
            serviceRequest.birthday(),
            serviceRequest.mbti(),
            serviceRequest.calendarColor());
    }
}
