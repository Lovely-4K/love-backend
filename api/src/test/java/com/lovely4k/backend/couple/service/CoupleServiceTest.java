package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
class CoupleServiceTest extends IntegrationTestSupport {

    @Autowired
    private CoupleService coupleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoupleRepository coupleRepository;

    @Test
    @DisplayName("초대 코드를 발급받을 수 있다.")
    void createInvitationCode() throws Exception {
        //given
        Member savedMember = memberRepository.save(createMember());

        //when
        InvitationCodeCreateResponse response = coupleService.createInvitationCode(savedMember.getId());

        //then
        Couple findCouple = coupleRepository.findById(response.coupleId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 커플 id입니다."));

        assertAll(
            () -> assertThat(findCouple.getId()).isNotNull(),
            () -> assertThat(findCouple.getInvitationCode()).isNotNull()
        );
    }

    private Member createMember() {
        return Member.builder()
            .sex("MALE")
            .name("김철수")
            .nickname("듬직이")
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti("ESFJ")
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }

}