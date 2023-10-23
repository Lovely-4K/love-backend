package com.lovely4k.docs.diary;

import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.controller.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DiaryControllerDocsTest extends RestDocsSupport {

    private final DiaryService diaryService = mock(DiaryService.class);

    @Override
    protected Object initController() {
        return new DiaryController(diaryService);
    }

    @DisplayName("다이어리를 작성하는 API")
    @Test
    void createDiary() throws Exception{
        LocalDate localDate = LocalDate.of(2023, 10, 20);
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 5, "2023-10-20", "ACCOMODATION", "test Text");

        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("diary-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("kakaoId").type(JsonFieldType.STRING).description("카카오 장소 id"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("장소 주소"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("장소에 대한 평점"),
                                fieldWithPath("datingDay").type(JsonFieldType.STRING).description("데이트 한 날짜"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("장소 카테고리"),
                                fieldWithPath("text").type(JsonFieldType.STRING).description("다이어리 내용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디")

                        )
                        )


                )
        ;
    }
}
