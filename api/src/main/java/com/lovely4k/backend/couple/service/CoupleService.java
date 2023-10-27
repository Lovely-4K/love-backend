package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.request.CoupleProfileEditServiceRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InvitationCodeCreateResponse createInvitationCode(Long requestedMemberId) {
        String invitationCode = UUID.randomUUID().toString();

        // 코드 발급자의 성별 구분해서 저장할것. 현재는 남자로 가정.
        Couple couple = Couple.builder()
            .boyId(requestedMemberId)
            .girlId(null)
            .meetDay(null)
            .invitationCode(invitationCode)
            .build();

        Couple savedCouple = coupleRepository.save(couple);

        return new InvitationCodeCreateResponse(savedCouple.getId(), invitationCode);
    }

    // 동일한 초대 코드가 여러개일 경우 어떻게 처리할지
    @Transactional
    public void registerCouple(String invitationCode, Long receivedMemberId) {
        Couple couple = validateInvitationCode(invitationCode);

        couple.registerLover(receivedMemberId);

        registerCoupleId(couple);
    }

    public CoupleProfileGetResponse getCoupleProfile(Long coupleId) {

        Couple couple = validateCoupleId(coupleId);

        Member boy = findMember(couple.getBoyId());
        Member girl = findMember(couple.getGirlId());

        return CoupleProfileGetResponse.fromEntity(boy, girl);
    }

    @Transactional
    public void updateCoupleProfile(CoupleProfileEditServiceRequest request, Long memberId) {
        Long coupleId = findMember(memberId).getCoupleId();
        Couple couple = validateCoupleId(coupleId);

        couple.update(request.meetDay());
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 id 입니다."));
    }

    private Couple validateCoupleId(Long coupleId) {
        return coupleRepository.findById(coupleId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));
    }

    private Couple validateInvitationCode(String invitationCode) {
        return coupleRepository.findByInvitationCode(invitationCode)
            .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 초대코드 입니다."));
    }

    private void registerCoupleId(Couple couple) {
        Member boy = findMember(couple.getBoyId());
        Member girl = findMember(couple.getGirlId());
        boy.registerCoupleId(couple.getId());
        girl.registerCoupleId(couple.getId());
    }
}
