package com.lovely4k.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovely4k.backend.couple.controller.CoupleController;
import com.lovely4k.backend.couple.service.CoupleService;
import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.authentication.OAuth2UserService;
import com.lovely4k.backend.member.authentication.SecurityConfig;
import com.lovely4k.backend.member.authentication.SessionUser;
import com.lovely4k.backend.member.controller.MemberController;
import com.lovely4k.backend.member.service.MemberService;
import com.lovely4k.backend.question.controller.QuestionController;
import com.lovely4k.backend.question.service.QuestionQueryService;
import com.lovely4k.backend.question.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WithMockUser(roles = "USER")
@WebMvcTest(controllers = {
    DiaryController.class,
    MemberController.class,
    QuestionController.class,
    CoupleController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    WebApplicationContext context;

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
    protected QuestionQueryService questionQueryService;

    @MockBean
    OAuth2UserService oAuth2UserService;

    protected MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new MockHttpSession();
        SessionUser sessionUser = new SessionUser(1L, 1L, Sex.MALE, "helloworld", "profile-image"); // Replace with actual constructor
        // Set necessary fields on sessionUser
        mockSession.setAttribute("member", sessionUser);

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/v1/**").session(mockSession))
            .defaultRequest(post("/v1/**").session(mockSession))
            .defaultRequest(patch("/v1/**").session(mockSession))
            .defaultRequest(delete("/v1/**").session(mockSession))
            .defaultRequest(multipart("/v1/**").session(mockSession))
            .build();
    }
}
