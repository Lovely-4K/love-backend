package com.lovely4k.docs.calendar;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lovely4k.backend.calendar.ScheduleType;
import com.lovely4k.backend.calendar.controller.CalendarController;
import com.lovely4k.backend.calendar.controller.request.CreateCalendarRequest;
import com.lovely4k.backend.calendar.controller.request.UpdateCalendarRequest;
import com.lovely4k.backend.calendar.repository.FindCalendarsWithDateRepositoryRequest;
import com.lovely4k.backend.calendar.repository.response.FindCalendarsWithDateResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import com.lovely4k.backend.calendar.service.CalendarCommandService;
import com.lovely4k.backend.calendar.service.CalendarQueryService;
import com.lovely4k.backend.calendar.service.request.CreateCalendarServiceReqeust;
import com.lovely4k.backend.calendar.service.request.UpdateCalendarServiceRequest;
import com.lovely4k.backend.calendar.service.response.*;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CalendarApiDocsTest extends RestDocsSupport {

    private final CalendarCommandService calendarCommandService = mock(CalendarCommandService.class);
    private final CalendarQueryService calendarQueryService = mock(CalendarQueryService.class);

    @Override
    protected Object initController() {
        return new CalendarController(calendarCommandService, calendarQueryService);
    }

    @DisplayName("from ~ to 기간에 해당하는 일정 조회 api docs")
    @Test
    void findAllSchedulesWithDate() throws Exception {
        // Given
        FindCalendarsWithDateServiceResponse response = FindCalendarsWithDateServiceResponse
            .from(
                List.of(
                    new FindCalendarsWithDateResponse(
                        1L,
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


        given(calendarQueryService.findCalendarsWithDate(any(FindCalendarsWithDateRepositoryRequest.class)))
            .willReturn(response);

        mockMvc.perform(get("/v1/calendars")
                .queryParam("from", "2023-01-01")
                .queryParam("to", "2023-01-31")
                .queryParam("coupleId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("find-all-schedules-with-date",
                queryParameters(
                    parameterWithName("from").description("조회 시작 날짜"),
                    parameterWithName("to").description("조회 종료 날짜"),
                    parameterWithName("coupleId").description("커플 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.colorInfo.boyId").type(NUMBER).description("남자의 회원 ID"),
                    fieldWithPath("body.colorInfo.boyCalendarColor").type(STRING).description("남자의 달력 색상"),
                    fieldWithPath("body.colorInfo.girlId").type(NUMBER).description("여자의 회원 ID"),
                    fieldWithPath("body.colorInfo.girlCalendarColor").type(STRING).description("여자의 달력 색상"),
                    fieldWithPath("body.schedules[0].calendarId").type(NUMBER).description("일정의 id"),
                    fieldWithPath("body.schedules[0].startDate").type(STRING).description("일정 시작 날짜"),
                    fieldWithPath("body.schedules[0].endDate").type(STRING).description("일정 종료 날짜"),
                    fieldWithPath("body.schedules[0].scheduleDetails").type(STRING).description("일정 상세"),
                    fieldWithPath("body.schedules[0].scheduleType").type(STRING).description("일정 타입(공통 일정이 아닌 경우 PRIVATE로 반환)"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }

    @DisplayName("최근 일정을 조회하는 api docs")
    @Test
    void findRecentSchedules() throws Exception {
        // Given
        FindRecentCalendarsServiceResponse response = FindRecentCalendarsServiceResponse
            .from(
                List.of(
                    new FindRecentCalendarsResponse(
                        1L,
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

        given(calendarQueryService.findRecentCalendars(anyLong(), anyInt()))
            .willReturn(response);

        mockMvc.perform(get("/v1/calendars/recent")
                .queryParam("coupleId", "1")
                .queryParam("limit", "5")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("find-recent-schedules",
                queryParameters(
                    parameterWithName("coupleId").description("커플 ID"),
                    parameterWithName("limit").description("조회할 개수 default 5")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.colorInfo.boyId").type(NUMBER).description("남자의 회원 ID"),
                    fieldWithPath("body.colorInfo.boyCalendarColor").type(STRING).description("남자의 달력 색상"),
                    fieldWithPath("body.colorInfo.girlId").type(NUMBER).description("여자의 회원 ID"),
                    fieldWithPath("body.colorInfo.girlCalendarColor").type(STRING).description("여자의 달력 색상"),
                    fieldWithPath("body.schedules[0].calendarId").type(NUMBER).description("일정의 id"),
                    fieldWithPath("body.schedules[0].startDate").type(STRING).description("일정 시작 날짜"),
                    fieldWithPath("body.schedules[0].endDate").type(STRING).description("일정 종료 날짜"),
                    fieldWithPath("body.schedules[0].scheduleDetails").type(STRING).description("일정 상세"),
                    fieldWithPath("body.schedules[0].scheduleType").type(STRING).description("일정 타입(공통 일정이 아닌 경우 PRIVATE로 반환)"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }

    @DisplayName("일정을 생성하는 api docs")
    @Test
    void createSchedule() throws Exception {
        // Given
        CreateCalendarRequest request = new CreateCalendarRequest(LocalDate.now(), LocalDate.now(), "details", "DATE");

        CreateCalendarResponse response = new CreateCalendarResponse(1L, LocalDate.now(), LocalDate.now(), "details", ScheduleType.DATE, 0L);

        given(calendarCommandService.createCalendar(anyLong(), anyLong(), any(CreateCalendarServiceReqeust.class)))
            .willReturn(response);

        mockMvc.perform(post("/v1/calendars")
                .queryParam("coupleId", "1")
                .queryParam("memberId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(request))
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("create-schedules",
                queryParameters(
                    parameterWithName("coupleId").description("커플 ID"),
                    parameterWithName("memberId").description("일정 기록자 id")
                ),
                requestFields(
                    fieldWithPath("startDate").type(STRING).description("일정 시작 날짜"),
                    fieldWithPath("endDate").type(STRING).description("일정 종료 날짜"),
                    fieldWithPath("scheduleDetails").type(STRING).description("일정에 대한 상세 설명"),
                    fieldWithPath("scheduleType").type(STRING).description("일정의 유형 (DATE, TIME 등)")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.id").type(NUMBER).description("생성된 일정의 ID"),
                    fieldWithPath("body.startDate").type(STRING).description("일정 시작 날짜"),
                    fieldWithPath("body.endDate").type(STRING).description("일정 종료 날짜"),
                    fieldWithPath("body.scheduleDetails").type(STRING).description("일정 상세 정보"),
                    fieldWithPath("body.scheduleType").type(STRING).description("일정 유형"),
                    fieldWithPath("body.ownerId").type(NUMBER).description("일정의 주인 아이디(공유 일정일 경우 0)"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }

    @DisplayName("일정을 수정하는 api docs")
    @Test
    void editScheduleById() throws Exception {
        // Given
        Long scheduleId = 1L;
        UpdateCalendarRequest updateRequest = new UpdateCalendarRequest(LocalDate.now(), LocalDate.now(), "updated details", "DATE");
        UpdateCalendarResponse updateResponse = new UpdateCalendarResponse(LocalDate.now(), LocalDate.now(), "updated details", ScheduleType.DATE);

        given(calendarCommandService.updateCalendarById(eq(scheduleId), any(UpdateCalendarServiceRequest.class)))
            .willReturn(updateResponse);

        // When & Then
        mockMvc.perform(patch("/v1/calendars/{id}", scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(updateRequest))
                .characterEncoding("UTF-8"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("edit-schedule",
                pathParameters(
                    parameterWithName("id").description("일정 ID")
                ),
                requestFields(
                    fieldWithPath("startDate").type(STRING).description("일정 시작 날짜"),
                    fieldWithPath("endDate").type(STRING).description("일정 종료 날짜"),
                    fieldWithPath("scheduleDetails").type(STRING).description("일정에 대한 상세 설명"),
                    fieldWithPath("scheduleType").type(STRING).description("일정의 유형 (DATE, TIME 등)")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("응답 코드"),
                    fieldWithPath("body.startDate").type(STRING).description("수정된 일정 시작 날짜"),
                    fieldWithPath("body.endDate").type(STRING).description("수정된 일정 종료 날짜"),
                    fieldWithPath("body.scheduleDetails").type(STRING).description("수정된 일정 상세 정보"),
                    fieldWithPath("body.scheduleType").type(STRING).description("수정된 일정 유형"),
                    fieldWithPath("links[0].rel").type(STRING).description("URL과의 관계"),
                    fieldWithPath("links[0].href").type(STRING).description("URL의 링크")
                )
            ));
    }

    @DisplayName("일정을 삭제하는 API 테스트")
    @Test
    void deleteScheduleById() throws Exception {
        // Given
        Long scheduleId = 1L;

        willDoNothing().given(calendarCommandService).deleteCalendarById(scheduleId);

        // When & Then
        mockMvc.perform(delete("/v1/calendars/{id}", scheduleId))
            .andExpect(status().isNoContent())
            .andDo(print())
            .andDo(document("delete-schedule",
                pathParameters(
                    parameterWithName("id").description("삭제할 일정의 ID")
                )
            ));
    }

}