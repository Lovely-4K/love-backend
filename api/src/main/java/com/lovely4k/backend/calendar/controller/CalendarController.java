package com.lovely4k.backend.calendar.controller;

import com.lovely4k.backend.calendar.controller.request.CreateCalendarRequest;
import com.lovely4k.backend.calendar.controller.request.FindAllCalendarsWithDateRequest;
import com.lovely4k.backend.calendar.controller.request.UpdateCalendarRequest;
import com.lovely4k.backend.calendar.service.CalendarCommandService;
import com.lovely4k.backend.calendar.service.CalendarQueryService;
import com.lovely4k.backend.calendar.service.response.CreateCalendarResponse;
import com.lovely4k.backend.calendar.service.response.FindAllCalendarsWithDateServiceResponse;
import com.lovely4k.backend.calendar.service.response.FindRecentCalendarsServiceResponse;
import com.lovely4k.backend.calendar.service.response.UpdateCalendarResponse;
import com.lovely4k.backend.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<FindAllCalendarsWithDateServiceResponse>> findAllSchedulesWithDate(@ModelAttribute FindAllCalendarsWithDateRequest request) {
        return ApiResponse.ok(calendarQueryService.findAllCalendarsWithDate(request.toServiceDto()),
                linkTo(methodOn(getClass()).findAllSchedulesWithDate(request)).withSelfRel(),
                linkTo(getClass().getMethod(CREATE_SCHEDULE, Long.class, CreateCalendarRequest.class)).withRel(CREATE_SCHEDULE),
                linkTo(getClass().getMethod(EDIT_SCHEDULE_BY_ID, Long.class, UpdateCalendarRequest.class)).withRel(EDIT_SCHEDULE_BY_ID),
                linkTo(getClass().getMethod(DELETE_SCHEDULE_BY_ID, Long.class)).withRel(DELETE_SCHEDULE_BY_ID));
    }

    @SneakyThrows
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<FindRecentCalendarsServiceResponse>> findRecentSchedules(@RequestParam("coupleId") Long coupleId, @RequestParam(value = "limit", defaultValue = "5") Long limit) {
        return ApiResponse.ok(calendarQueryService.findRecentCalendars(coupleId, limit),
                linkTo(methodOn(getClass()).findRecentSchedules(coupleId, limit)).withSelfRel(),
                linkTo(getClass().getMethod(EDIT_SCHEDULE_BY_ID, Long.class, UpdateCalendarRequest.class)).withRel(EDIT_SCHEDULE_BY_ID));
    }

    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreateCalendarResponse>> createSchedule(@RequestParam("coupleId") Long coupleId, @RequestBody CreateCalendarRequest request) {
        CreateCalendarResponse response = calendarCommandService.createCalendar(coupleId, request.toServiceDto());

        return ApiResponse.created(response, response.id(),
                linkTo(methodOn(getClass()).createSchedule(coupleId, request)).withSelfRel(),
                linkTo(getClass().getMethod(FIND_ALL_SCHEDULE_WITH_DATE, FindAllCalendarsWithDateRequest.class)).withRel(FIND_ALL_SCHEDULE_WITH_DATE));
    }

    @SneakyThrows
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UpdateCalendarResponse>> editScheduleById(@PathVariable("id") Long id, @RequestBody UpdateCalendarRequest request) {
        UpdateCalendarResponse response = calendarCommandService.updateCalendarById(id, request.toServiceDto());
        return ApiResponse.ok(response,
                linkTo(methodOn(getClass()).editScheduleById(id, request)).withSelfRel(),
                linkTo(getClass().getMethod(FIND_ALL_SCHEDULE_WITH_DATE, FindAllCalendarsWithDateRequest.class)).withRel(FIND_ALL_SCHEDULE_WITH_DATE));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleById(@PathVariable("id") Long id) {
        calendarCommandService.deleteCalendarById(id);
        return ResponseEntity.noContent().build();
    }

}