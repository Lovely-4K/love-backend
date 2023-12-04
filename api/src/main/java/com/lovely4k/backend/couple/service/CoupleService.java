package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleQueryRepository;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.repository.response.FindCoupleProfileResponse;
import com.lovely4k.backend.couple.service.request.CoupleProfileEditServiceRequest;
import com.lovely4k.backend.couple.service.response.CoupleProfileGetResponse;
import com.lovely4k.backend.couple.service.response.CoupleTemperatureResponse;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;
    private final CoupleQueryRepository coupleQueryRepository;

    @Transactional
    public InvitationCodeCreateResponse createInvitationCode(Long requestedMemberId, String sex) {
        String invitationCode = UUID.randomUUID().toString();

        Couple couple = Couple.create(requestedMemberId, Sex.valueOf(sex), invitationCode);
        Couple savedCouple = coupleRepository.save(couple);
        return new InvitationCodeCreateResponse(savedCouple.getId(), invitationCode);
    }

    @Transactional
    @CacheEvict(value = {CacheConstants.USER_DETAILS, CacheConstants.COUPLE_PROFILE}, allEntries = true)
    public void registerCouple(String invitationCode, Long receivedMemberId) {
        Couple couple = validateInvitationCode(invitationCode);
        couple.registerPartnerId(receivedMemberId);
        registerProfileInfo(couple);
    }

    @Cacheable(value = CacheConstants.COUPLE_PROFILE, key = "#memberId")
    public CoupleProfileGetResponse findCoupleProfile(Long memberId) {

        FindCoupleProfileResponse response = coupleQueryRepository.findCoupleProfile(memberId);
        return CoupleProfileGetResponse.from(response);
    }

    @Transactional
    @CacheEvict(value = CacheConstants.COUPLE_PROFILE, key = "#memberId")
    public void updateCoupleProfile(CoupleProfileEditServiceRequest request, Long memberId) {
        Long coupleId = findMember(memberId).getCoupleId();
        Couple couple = findCouple(coupleId);

        couple.update(request.meetDay());
    }

    @CacheEvict(value = CacheConstants.LOVE_TEMPERATURE, key = "#coupleId")
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void increaseTemperature(Long coupleId) {
        Couple couple = coupleRepository.findByIdWithOptimisticLock(coupleId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id 입니다."));  // NOSONAR
        couple.increaseTemperature();
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.COUPLE_PROFILE, allEntries = true),
        @CacheEvict(value = CacheConstants.LOVE_TEMPERATURE, key = "#coupleId")
    })
    public void deleteCouple(Long coupleId, Long memberId) {
        Couple couple = findCouple(coupleId);
        couple.checkAuthority(memberId);
        coupleRepository.delete(couple);
    }

    @Transactional
    @CacheEvict(value = CacheConstants.COUPLE_PROFILE, allEntries = true)
    public void reCouple(LocalDate requestedDate, Long coupleId, Long memberId) {
        Couple couple = findDeletedCouple(coupleId);
        Long opponentId = couple.getOpponentId(memberId);
        findMember(opponentId).checkReCoupleCondition(coupleId);

        couple.recouple(memberId, requestedDate);
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

    private void registerProfileInfo(Couple couple) {
        List<Member> coupleMembers = memberRepository.findCoupleMembersById(couple.getBoyId(),couple.getGirlId());

        Member boy = findMember(couple.getBoyId(), coupleMembers);
        Member girl = findMember(couple.getGirlId(), coupleMembers);

        boy.registerProfileInfo(couple.getId());
        girl.registerProfileInfo(couple.getId());
    }

    private Member findMember(Long memberId, List<Member> coupleMembers) {
        return coupleMembers.stream()
            .filter(member -> member.getId().equals(memberId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 id 입니다."));

    }

    @Cacheable(value = CacheConstants.LOVE_TEMPERATURE, key = "#coupleId")
    public CoupleTemperatureResponse findTemperature(Long coupleId) {
        Couple couple = findCouple(coupleId);

        return new CoupleTemperatureResponse(couple.getTemperature());
    }
}