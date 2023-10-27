package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
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

    @Transactional
    public InvitationCodeCreateResponse createInvitationCode(Long requestedMemberId) {
        String invitationCode = UUID.randomUUID().toString();

        // TODO : 코드 발급자의 성별 구분해서 저장할것. 현재는 남자로 가정.
        Couple couple = Couple.builder()
            .boyId(requestedMemberId)
            .girlId(null)
            .meetDay(null)
            .invitationCode(invitationCode)
            .build();

        Couple savedCouple = coupleRepository.save(couple);

        return new InvitationCodeCreateResponse(savedCouple.getId(), invitationCode);
    }

    // TODO : 동일한 초대 코드가 여러개일 경우 어떻게 처리할지
    @Transactional
    public void registerCouple(String invitationCode, Long receivedMemberId) {
        Couple couple = validateInvitationCode(invitationCode);

        couple.registerLover(receivedMemberId);
    }

    private Couple validateInvitationCode(String invitationCode) {
        return coupleRepository.findByInvitationCode(invitationCode)
            .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 초대코드 입니다."));
    }
}
