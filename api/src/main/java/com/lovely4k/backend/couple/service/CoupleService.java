package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.common.ExceptionMessage;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.request.CoupleProfileEditServiceRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InvitationCodeCreateResponse createInvitationCode(Long requestedMemberId, Sex sex) {
        String invitationCode = UUID.randomUUID().toString();

        Couple couple = Couple.create(requestedMemberId, sex, invitationCode);
        Couple savedCouple = coupleRepository.save(couple);

        return new InvitationCodeCreateResponse(savedCouple.getId(), invitationCode);
    }

    @Transactional
    public void registerCouple(String invitationCode, Long receivedMemberId) {
        Couple couple = validateInvitationCode(invitationCode);

        if (couple.getBoyId() == null) {
            couple.registerBoyId(receivedMemberId);
        } else {
            couple.registerGirlId(receivedMemberId);
        }

        registerCoupleId(couple);
    }

    public CoupleProfileGetResponse findCoupleProfile(Long memberId) {

        Long coupleId = findMember(memberId).getCoupleId();

        Couple couple = findCouple(coupleId);

        Optional<Member> boy = findMemberOptional(Optional.ofNullable(couple.getBoyId()));
        Optional<Member> girl = findMemberOptional(Optional.ofNullable(couple.getGirlId()));

        return CoupleProfileGetResponse.from(boy, girl, couple.getMeetDay());
    }

    @Transactional
    public void updateCoupleProfile(CoupleProfileEditServiceRequest request, Long memberId) {
        Long coupleId = findMember(memberId).getCoupleId();
        Couple couple = findCouple(coupleId);

        couple.update(request.meetDay());
    }

    @Transactional
    public void increaseTemperature(Long coupleId) {
        Couple couple = coupleRepository.findByIdWithOptimisticLock(coupleId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));
        couple.increaseTemperature();
    }
  
    @Transactional
    public void deleteCouple(Long coupleId, Long memberId) {
        Couple couple = findCouple(coupleId);
        if (!couple.hasAuthority(memberId)) {
            throw new IllegalArgumentException(ExceptionMessage.noAuthorityMessage("member", memberId, "couple", coupleId));
        }
        coupleRepository.delete(couple);
    }
  
    private Optional<Member> findMemberOptional(Optional<Long> memberId) {
        return memberRepository.findById(memberId.orElse(-1L));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 id 입니다."));
    }

    private Couple findCouple(Long coupleId) {
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
