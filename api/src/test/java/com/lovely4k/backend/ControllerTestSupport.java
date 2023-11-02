package com.lovely4k.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovely4k.backend.calendar.controller.CalendarController;
import com.lovely4k.backend.calendar.service.CalendarCommandService;
import com.lovely4k.backend.calendar.service.CalendarQueryService;
import com.lovely4k.backend.couple.controller.CoupleController;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.member.controller.MemberController;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.question.controller.QuestionController;
import com.lovely4k.backend.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
    DiaryController.class,
    MemberController.class,
    QuestionController.class,
    CoupleController.class,
    CalendarController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected DiaryService diaryService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected QuestionService questionService;

    @MockBean
    protected CoupleService coupleService;

    @MockBean
    protected CalendarCommandService calendarCommandService;

    @MockBean
    protected CalendarQueryService calendarQueryService;
}