package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.Calendar;
import com.lovely4k.backend.calendar.repository.CalendarCommandRepository;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.calendar.service.request.UpdateCalendarServiceRequest;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import com.lovely4k.backend.calendar.service.response.UpdateCalendarResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lovely4k.backend.common.ExceptionMessage.notFoundEntityMessage;

@Transactional
@RequiredArgsConstructor
@Service
public class CalendarCommandService {

    private final CalendarCommandRepository calendarCommandRepository;

    public CreateCalendarResponse createCalendar(Long coupleId, Long memberId, CreateCalendarServiceReqeust serviceDto) {
        Calendar savedCalendar = calendarCommandRepository.save(serviceDto.toEntity(coupleId, memberId));
        return CreateCalendarResponse.from(savedCalendar);
    }

    public UpdateCalendarResponse updateCalendarById(Long id, UpdateCalendarServiceRequest request) {
        Calendar calendar = calendarCommandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("calendar", id)));
        calendar.update(request.startDate(), request.endDate(), request.scheduleType(), request.scheduleDetails());

        return UpdateCalendarResponse.from(calendar);
    }

    public void deleteCalendarById(Long id) {
        if (!calendarCommandRepository.existsById(id)) {
            throw new EntityNotFoundException((notFoundEntityMessage("calendar", id)));
        }

        calendarCommandRepository.deleteById(id);
    }
}