package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.CalendarQueryRepository;
import com.lovely4k.backend.calendar.repository.response.FindAllCalendarsWithDateResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import com.lovely4k.backend.calendar.service.response.FindAllCalendarsWithDateServiceRequest;
import com.lovely4k.backend.calendar.service.response.FindAllCalendarsWithDateServiceResponse;
import com.lovely4k.backend.calendar.service.response.FindRecentCalendarsServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CalendarQueryService {

    private final CalendarQueryRepository calendarQueryRepository;

    public FindAllCalendarsWithDateServiceResponse findAllCalendarsWithDate(FindAllCalendarsWithDateServiceRequest request) {

        return FindAllCalendarsWithDateServiceResponse
            .from(
                List.of(
                    new FindAllCalendarsWithDateResponse(
                        1L,
                        "RED",
                        2L,
                        "BLUE",
                        LocalDate.now(),
                        LocalDate.now(),
                        "영화보기",
                        ScheduleType.DATE)
                )
            );
    }

    public FindRecentCalendarsServiceResponse findRecentCalendars(Long coupleId, Integer limit) {
        List<FindRecentCalendarsResponse> result = calendarQueryRepository.findRecentCalendarsWithColors(coupleId, limit);
        return FindRecentCalendarsServiceResponse.from(result);
    }
}