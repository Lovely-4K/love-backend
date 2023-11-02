package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Recovery;
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

    @DisplayName("findByCoupleId을 통해 Recovery 엔티티를 조회할 수 있다. ")
    @Test
    void findByCoupleId() {
        // given
        LocalDate requestDate = LocalDate.of(2020, 10, 20);
        Recovery recovery = Recovery.of(1L, requestDate);
        recoveryRepository.save(recovery);

        // when
        Optional<Recovery> optionalRecovery = recoveryRepository.findByCoupleId(1L);

        // then
        assertAll(
                () -> assertThat(optionalRecovery).isPresent(),
                () -> assertThat(optionalRecovery.get().getCoupleId()).isEqualTo(1L),
                () -> assertThat(optionalRecovery.get().getRequestedDate()).isEqualTo(requestDate)
        );
    }

}
