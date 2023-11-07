package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.repository.CalendarQueryRepository;
import com.lovely4k.backend.calendar.repository.response.ColorResponse;
import com.lovely4k.backend.calendar.service.response.FindAllCalendarsWithDateServiceRequest;
import com.lovely4k.backend.calendar.service.response.FindAllCalendarsWithDateServiceResponse;
import com.lovely4k.backend.calendar.service.response.FindRecentCalendarsServiceResponse;
import com.lovely4k.backend.calendar.service.response.ScheduleServiceResponse;
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
        calendarQueryRepository.findAll();
        return new FindAllCalendarsWithDateServiceResponse(
                new ColorResponse(1L, "yellow"),
                new ColorResponse(2L, "green"),
                List.of(new ScheduleServiceResponse(LocalDate.now(), LocalDate.now(), "놀러가기", ScheduleType.DATE))
        );
    }

    public FindRecentCalendarsServiceResponse findRecentCalendars(Long coupleId, Long limit) {
        return new FindRecentCalendarsServiceResponse(
                new ColorResponse(1L, "yellow"),
                new ColorResponse(2L, "green"),
                List.of(new ScheduleServiceResponse(LocalDate.now(), LocalDate.now(), "놀러가기", ScheduleType.DATE))
        );
    }
}