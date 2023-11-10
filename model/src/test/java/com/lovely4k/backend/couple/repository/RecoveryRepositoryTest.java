package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.Recovery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecoveryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    RecoveryRepository recoveryRepository;

    @Autowired
    CoupleRepository coupleRepository;

    @AfterEach
    void tearDown() {
        recoveryRepository.deleteAllInBatch();
    }

    @DisplayName("findByCoupleId을 통해 Recovery 엔티티를 조회할 수 있다. ")
    @Test
    void findByCoupleId() {
        // given
        Couple couple = buildCouple();
        Couple savedCouple = coupleRepository.save(couple);

        LocalDate requestDate = LocalDate.of(2020, 10, 20);
        Recovery recovery = Recovery.of(savedCouple.getId(), requestDate);
        recoveryRepository.save(recovery);

        // when
        Optional<Recovery> optionalRecovery = recoveryRepository.findByCoupleId(savedCouple.getId());

        // then
        assertAll(
            () -> assertThat(optionalRecovery).isPresent(),
            () -> assertThat(optionalRecovery.get().getCoupleId()).isEqualTo(savedCouple.getId())
        );
    }

    private Couple buildCouple() {
        return Couple.builder()
            .boyId(1L)
            .girlId(2L)
            .meetDay(LocalDate.of(2020, 10, 20))
            .invitationCode("test-code")
            .deleted(true)
            .deletedDate(LocalDate.of(2022, 10, 20))
            .build();
    }

}
