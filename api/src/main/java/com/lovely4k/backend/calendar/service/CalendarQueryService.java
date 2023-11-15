package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.repository.CalendarQueryRepository;
import com.lovely4k.backend.calendar.repository.FindCalendarsWithDateRepositoryRequest;
import com.lovely4k.backend.calendar.repository.response.FindCalendarsWithDateResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import com.lovely4k.backend.calendar.service.response.FindCalendarsWithDateServiceResponse;
import com.lovely4k.backend.calendar.service.response.FindRecentCalendarsServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CalendarQueryService {

    private final CalendarQueryRepository calendarQueryRepository;

    public FindCalendarsWithDateServiceResponse findCalendarsWithDate(FindCalendarsWithDateRepositoryRequest request, Long coupleId) {
        List<FindCalendarsWithDateResponse> responses = calendarQueryRepository.findCalendarsWithDate(request, coupleId);
        return FindCalendarsWithDateServiceResponse.from(responses);
    }

    public FindRecentCalendarsServiceResponse findRecentCalendars(Long coupleId, Integer limit) {
        List<FindRecentCalendarsResponse> result = calendarQueryRepository.findRecentCalendarsWithColors(coupleId, limit);
        return FindRecentCalendarsServiceResponse.from(result);
    }
}