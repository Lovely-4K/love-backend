package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.repository.CalendarCommandRepository;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.calendar.service.request.UpdateCalendarServiceRequest;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import com.lovely4k.backend.calendar.service.response.UpdateCalendarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CalendarCommandService {

    private final CalendarCommandRepository calendarCommandRepository;

    public CreateCalendarResponse createCalendar(Long coupleId, CreateCalendarServiceReqeust serviceDto) {
        calendarCommandRepository.findById(coupleId);
        return CreateCalendarResponse.from();
    }

    public UpdateCalendarResponse updateCalendarById(Long id, UpdateCalendarServiceRequest request) {
        return UpdateCalendarResponse.from();
    }

    public void deleteCalendarById(Long id) {
    }
}