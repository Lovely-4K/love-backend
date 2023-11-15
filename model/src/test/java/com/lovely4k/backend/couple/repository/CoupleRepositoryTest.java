package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CoupleRepositoryTest extends IntegrationTestSupport {

    @Autowired
    CoupleRepository coupleRepository;

    @DisplayName("findDeletedById는 삭제된 Entity도 조회할 수 있다.")
    @Test
    void findDeletedById() {
        // given
        Couple couple = Couple.builder()
                .boyId(1L)
                .girlId(2L)
                .meetDay(LocalDate.of(2020, 1, 20))
                .invitationCode("test-code")
                .deleted(true)
                .deletedDate(LocalDate.of(2020, 5, 30))
                .build();

        Couple savedCouple = coupleRepository.save(couple);

        // when
        Optional<Couple> optionalCouple = coupleRepository.findDeletedById(savedCouple.getId());

        // then
        assertAll(
                () -> assertThat(optionalCouple).isPresent(),
                () -> assertThat(optionalCouple.get().getId()).isEqualTo(savedCouple.getId())
        );
    }


}
