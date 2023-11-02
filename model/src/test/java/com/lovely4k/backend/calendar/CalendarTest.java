package com.lovely4k.backend.calendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CalendarTest {

    @Test
    @DisplayName("유효한 세부 사항이 주어졌을 때 create가 호출되면 Calendar 객체가 반환되어야 한다")
    void givenValidDetails_whenCreate_thenCalendarObjectShouldBeReturned() {
        // 준비
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        long ownerId = 1L;
        String scheduleType = "PERSONAL";
        String scheduleDetails = "새해 계획";
        long coupleId = 2L;

        // 실행
        Calendar calendar = Calendar.create(startDate, endDate, ownerId, scheduleType, scheduleDetails, coupleId);

        // 검증
        assertThat(calendar).isNotNull()
                .extracting(Calendar::getStartDate, Calendar::getEndDate, Calendar::getOwnerId, Calendar::getScheduleType, Calendar::getScheduleDetails, Calendar::getCoupleId)
                .containsExactly(startDate, endDate, 0L, ScheduleType.valueOf(scheduleType), scheduleDetails, coupleId);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidArguments")
    @DisplayName("유효하지 않은 인자들이 주어졌을 때 create가 호출되면 적절한 예외가 발생해야 한다")
    void givenInvalidArguments_whenCreate_thenAppropriateExceptionShouldBeThrown(
            LocalDate startDate, LocalDate endDate, long ownerId, String scheduleType, String scheduleDetails, long coupleId, Class<? extends Throwable> expectedException, String expectedMessage) {

        assertThatThrownBy(() -> Calendar.create(startDate, endDate, ownerId, scheduleType, scheduleDetails, coupleId))
                .isInstanceOf(expectedException)
                .hasMessageContaining(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUpdateArguments")
    @DisplayName("유효하지 않은 인자들이 주어졌을 때 update가 호출되면 적절한 예외가 발생해야 한다")
    void givenInvalidArguments_whenUpdate_thenAppropriateExceptionShouldBeThrown(
            LocalDate startDate, LocalDate endDate, String scheduleType, String scheduleDetails, Class<? extends Throwable> expectedException, String expectedMessage) {
        Calendar calendar = Calendar.create(LocalDate.now(), LocalDate.now(), 1L, "DATE", "details", 1L);
        assertThatThrownBy(() -> calendar.update(startDate, endDate, scheduleType, scheduleDetails))
                .isInstanceOf(expectedException)
                .hasMessageContaining(expectedMessage);
    }


    private static Stream<Arguments> provideInvalidArguments() {
        LocalDate validStartDate = LocalDate.of(2023, 1, 1);
        LocalDate validEndDate = LocalDate.of(2023, 12, 31);
        long validOwnerId = 1L;
        String validScheduleType = "PERSONAL";
        String validScheduleDetails = "새해 계획";
        long validCoupleId = 2L;

        return Stream.of(
                // startDate 검증
                Arguments.of(null, validEndDate, validOwnerId, validScheduleType, validScheduleDetails, validCoupleId, NullPointerException.class, "startDate must not be null"),
                // endDate 검증
                Arguments.of(validStartDate, null, validOwnerId, validScheduleType, validScheduleDetails, validCoupleId, NullPointerException.class, "endDate must not be null"),
                Arguments.of(validStartDate, validStartDate.minusDays(1), validOwnerId, validScheduleType, validScheduleDetails, validCoupleId, IllegalArgumentException.class, "endDate must be after startDate"),
                // scheduleType 검증
                Arguments.of(validStartDate, validEndDate, validOwnerId, "INVALID_TYPE", validScheduleDetails, validCoupleId, IllegalArgumentException.class, "No enum constant"),
                // scheduleDetails 검증 비어있는 값
                Arguments.of(validStartDate, validEndDate, validOwnerId, validScheduleType, "", validCoupleId, IllegalArgumentException.class, "scheduleDetails must not be blank"),

                Arguments.of(validStartDate, validEndDate, validOwnerId, validScheduleType, "a".repeat(256), validCoupleId, IllegalArgumentException.class, "scheduleDetails must be between 1 and 255 characters long")
        );
    }

    private static Stream<Arguments> provideInvalidUpdateArguments() {
        LocalDate validStartDate = LocalDate.of(2023, 1, 1);
        LocalDate validEndDate = LocalDate.of(2023, 12, 31);
        String validScheduleType = "PERSONAL";
        String validScheduleDetails = "새해 계획";

        return Stream.of(
                // startDate 검증
                Arguments.of(null, validEndDate, validScheduleType, validScheduleDetails, NullPointerException.class, "startDate must not be null"),
                // endDate 검증
                Arguments.of(validStartDate, null, validScheduleType, validScheduleDetails,  NullPointerException.class, "endDate must not be null"),
                Arguments.of(validStartDate, validStartDate.minusDays(1), validScheduleType, validScheduleDetails, IllegalArgumentException.class, "endDate must be after startDate"),
                // scheduleType 검증
                Arguments.of(validStartDate, validEndDate, "INVALID_TYPE", validScheduleDetails, IllegalArgumentException.class, "No enum constant"),
                // scheduleDetails 검증 비어있는 값
                Arguments.of(validStartDate, validEndDate, validScheduleType, "", IllegalArgumentException.class, "scheduleDetails must not be blank"),

                Arguments.of(validStartDate, validEndDate, validScheduleType, "a".repeat(256), IllegalArgumentException.class, "scheduleDetails must be between 1 and 255 characters long")
        );
    }

}