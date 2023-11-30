package com.lovely4k.backend.calendar.controller;

import com.lovely4k.backend.calendar.controller.request.CreateCalendarRequest;
import com.lovely4k.backend.calendar.controller.request.FindCalendarsWithDateRequest;
import com.lovely4k.backend.calendar.controller.request.UpdateCalendarRequest;
import com.lovely4k.backend.calendar.service.CalendarCommandService;
import com.lovely4k.backend.calendar.service.CalendarQueryService;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import com.lovely4k.backend.calendar.service.response.FindCalendarsWithDateServiceResponse;
import com.lovely4k.backend.calendar.service.response.FindRecentCalendarsServiceResponse;
import com.lovely4k.backend.calendar.service.response.UpdateCalendarResponse;
import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.common.sessionuser.LoginUser;
import com.lovely4k.backend.common.sessionuser.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RequestMapping(value = "/v1/calendars", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class CalendarController {

    private final CalendarCommandService calendarCommandService;
    private final CalendarQueryService calendarQueryService;

    private static final String CREATE_SCHEDULE = "createSchedule";
    private static final String FIND_ALL_SCHEDULE_WITH_DATE = "findAllSchedulesWithDate";
    private static final String EDIT_SCHEDULE_BY_ID = "editScheduleById";
    private static final String DELETE_SCHEDULE_BY_ID = "deleteScheduleById";

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<FindCalendarsWithDateServiceResponse>> findAllSchedulesWithDate(@ModelAttribute @Valid FindCalendarsWithDateRequest request, @LoginUser SessionUser sessionUser) {
        return ApiResponse.ok(
                calendarQueryService.findCalendarsWithDate(request.toRepositoryDto(), sessionUser.coupleId(), sessionUser.memberId()),
                linkTo(methodOn(getClass()).findAllSchedulesWithDate(request, sessionUser)).withSelfRel(),
                linkTo(getClass().getMethod(CREATE_SCHEDULE, SessionUser.class, CreateCalendarRequest.class)).withRel(CREATE_SCHEDULE),
                linkTo(getClass().getMethod(EDIT_SCHEDULE_BY_ID, Long.class, UpdateCalendarRequest.class, SessionUser.class)).withRel(EDIT_SCHEDULE_BY_ID),
                linkTo(getClass().getMethod(DELETE_SCHEDULE_BY_ID, Long.class)).withRel(DELETE_SCHEDULE_BY_ID)
        );
    }

    @SneakyThrows
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<FindRecentCalendarsServiceResponse>> findRecentSchedules(
        @LoginUser SessionUser sessionUser,
        @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return ApiResponse.ok(
                calendarQueryService.findRecentCalendars(sessionUser.coupleId(), limit, sessionUser.memberId()),
                linkTo(methodOn(getClass()).findRecentSchedules(sessionUser, limit)).withSelfRel(),
                linkTo(getClass().getMethod(EDIT_SCHEDULE_BY_ID, Long.class, UpdateCalendarRequest.class, SessionUser.class)).withRel(EDIT_SCHEDULE_BY_ID)
        );
    }

    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreateCalendarResponse>> createSchedule(
            @LoginUser SessionUser sessionUser,
            @RequestBody @Valid CreateCalendarRequest request) {
        CreateCalendarResponse response = calendarCommandService.createCalendar(sessionUser.coupleId(), sessionUser.memberId(), request.toServiceDto());

        return ApiResponse.created(
                response,
                response.id(),
                linkTo(methodOn(getClass()).createSchedule(sessionUser, request)).withSelfRel(),
                linkTo(getClass().getMethod(FIND_ALL_SCHEDULE_WITH_DATE, FindCalendarsWithDateRequest.class, SessionUser.class)).withRel(FIND_ALL_SCHEDULE_WITH_DATE)
        );
    }

    @SneakyThrows
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UpdateCalendarResponse>> editScheduleById(@PathVariable("id") Long id, @RequestBody @Valid UpdateCalendarRequest request, @LoginUser SessionUser sessionUser) {
        UpdateCalendarResponse response = calendarCommandService.updateCalendarById(id, request.toServiceDto(), sessionUser.memberId());
        return ApiResponse.ok(
            response,
            linkTo(methodOn(getClass()).editScheduleById(id, request, sessionUser)).withSelfRel(),
            linkTo(getClass().getMethod(FIND_ALL_SCHEDULE_WITH_DATE, FindCalendarsWithDateRequest.class, SessionUser.class)).withRel(FIND_ALL_SCHEDULE_WITH_DATE)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleById(@PathVariable("id") Long id) {
        calendarCommandService.deleteCalendarById(id);
        return ResponseEntity.noContent().build();
    }

}