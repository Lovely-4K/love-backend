package com.lovely4k.docs.authentication;

import com.lovely4k.backend.authentication.trial_login.LoginController;
import com.lovely4k.backend.authentication.trial_login.LoginResponse;
import com.lovely4k.backend.authentication.trial_login.LoginService;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerDocsTest extends RestDocsSupport {

    private final LoginService loginService = mock(LoginService.class);

    @Override
    protected Object initController() {
        return new LoginController(loginService);
    }

    @DisplayName("체험용 아이디로 로그인 하는 API")
    @Test
    void trialLogin() throws Exception {
        // stubbing
        when(loginService.trialLogin(200395L)).thenReturn(new LoginResponse("ekq;jelkr", "qe;jkrqew;lkrjq;ewlrj"));

        mockMvc.perform(
                get("/v1/members/trial")
                    .characterEncoding("UTF-8")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("login-trial",
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("응답 코드"),
                    fieldWithPath("body.accessToken").type(JsonFieldType.STRING)
                        .description("access token value"),
                    fieldWithPath("body.refreshToken").type(JsonFieldType.STRING)
                        .description("refresh token value"),
                    fieldWithPath("links").type(JsonFieldType.ARRAY)
                        .description("relation links")
                )
            ));
    }
}
