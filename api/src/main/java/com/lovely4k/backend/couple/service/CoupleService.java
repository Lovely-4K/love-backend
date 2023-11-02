package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.common.ExceptionMessage;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.Recovery;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.repository.RecoveryRepository;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;
    private final RecoveryRepository recoveryRepository;

    @Transactional
    public InvitationCodeCreateResponse
    createInvitationCode(Long requestedMemberId, String sex) {
        String invitationCode = UUID.randomUUID().toString();

        Couple couple = Couple.create(requestedMemberId, Sex.valueOf(sex), invitationCode);
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
        checkAuthority(coupleId, memberId, couple);
        coupleRepository.delete(couple);
    }

    @Transactional
    public void reCouple(LocalDate requestedDate, Long coupleId, Long memberId) {
        Couple couple = findDeletedCouple(coupleId);
        checkAuthority(coupleId, memberId, couple);
        checkExpiration(requestedDate, couple);
        recoveryRepository.save(Recovery.of(coupleId, requestedDate));
    }

    private void checkAuthority(Long coupleId, Long memberId, Couple couple) {
        if (!couple.hasAuthority(memberId)) {
            throw new IllegalArgumentException(ExceptionMessage.noAuthorityMessage("member", memberId, "couple", coupleId));
        }
    }

    private void checkExpiration(LocalDate requestedDate, Couple couple) {
        if (couple.isExpired(requestedDate)) {
            throw new IllegalStateException("커플을 끊은 지 30일이 지났기 때문에 복원을 할 수 없습니다.");
        }
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

    private Couple findDeletedCouple(Long coupleId) {
        return coupleRepository.findDeletedById(coupleId)
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