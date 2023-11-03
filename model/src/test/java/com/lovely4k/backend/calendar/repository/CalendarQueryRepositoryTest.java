package com.lovely4k.backend.calendar.repository;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CalendarQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    CalendarQueryRepository calendarQueryRepository;

    @Sql(scripts = "/calendar.sql")
    @DisplayName("커플 id를 가지고 최근 일정, 커플의 구성원의 (id, 색상)을 조회할 수 있다.")
    @Test
    void findRecentCalendars() {
        // given -> calendar.sql
        int limit = 5;
        long coupleId = 1;

        // when
        List<FindRecentCalendarsResponse> result = calendarQueryRepository.findRecentCalendarsWithColors(coupleId, limit);

        // then
        assertAll("Recent Calendars",
            () -> assertThat(result).hasSize(limit),
            () -> assertThat(result.get(0).scheduleType()).isEqualTo(ScheduleType.DATE),
            () -> assertThat(result.get(0).boyCalendarColor()).isEqualTo("RED"),
            () -> assertThat(result.get(0).girlCalendarColor()).isEqualTo("BLUE")
        );
    }
}