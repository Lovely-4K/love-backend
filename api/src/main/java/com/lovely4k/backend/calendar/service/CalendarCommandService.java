package com.lovely4k.backend.calendar.service;

import com.lovely4k.backend.calendar.Calendar;
import com.lovely4k.backend.calendar.repository.CalendarCommandRepository;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.calendar.service.request.UpdateCalendarServiceRequest;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import com.lovely4k.backend.calendar.service.response.UpdateCalendarResponse;
import com.lovely4k.backend.common.cache.CacheConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lovely4k.backend.common.error.ExceptionMessage.notFoundEntityMessage;

@Transactional
@RequiredArgsConstructor
@Service
public class CalendarCommandService {

    private final CalendarCommandRepository calendarCommandRepository;

    @CacheEvict(value = {CacheConstants.CALENDAR_WITH_DATE, CacheConstants.RECENT_CALENDAR}, allEntries = true)
    public CreateCalendarResponse createCalendar(Long coupleId, Long ownerId, CreateCalendarServiceReqeust serviceDto) {
        Calendar savedCalendar = calendarCommandRepository.save(serviceDto.toEntity(coupleId, ownerId));
        return CreateCalendarResponse.from(savedCalendar);
    }

    @CacheEvict(value = {CacheConstants.CALENDAR_WITH_DATE, CacheConstants.RECENT_CALENDAR}, allEntries = true)
    public UpdateCalendarResponse updateCalendarById(Long id, UpdateCalendarServiceRequest request, Long loginUserId) {
        Calendar calendar = calendarCommandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("calendar", id)));
        calendar.update(request.startDate(), request.endDate(), request.scheduleType(), request.scheduleDetails(), loginUserId);

        return UpdateCalendarResponse.from(calendar);
    }

    @CacheEvict(value = {CacheConstants.CALENDAR_WITH_DATE, CacheConstants.RECENT_CALENDAR}, allEntries = true)
    public void deleteCalendarById(Long id) {
        if (!calendarCommandRepository.existsById(id)) {
            throw new EntityNotFoundException((notFoundEntityMessage("calendar", id)));
        }

        calendarCommandRepository.deleteById(id);
    }
}