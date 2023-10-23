package com.lovely4k.docs.diary;

import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                                        fieldWithPath("kakaoId").type(STRING).description("카카오 장소 id"),
                                        fieldWithPath("location").type(STRING).description("장소 주소"),
                                        fieldWithPath("score").type(NUMBER).description("장소에 대한 평점"),
                                        fieldWithPath("datingDay").type(STRING).description("데이트 한 날짜"),
                                        fieldWithPath("category").type(STRING).description("장소 카테고리"),
                                        fieldWithPath("text").type(STRING).description("다이어리 내용")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(NUMBER).description("코드"),
                                        fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디")
                                )
                        )
                )
        ;
    }

    @DisplayName("다이어리를 상세 조회하는 API")
    @Test
    void getDiaryDetail() throws Exception{
        this.mockMvc.perform(
                        get("/v1/diaries/{id}", 1L)
                                .header("memberId", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diary-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("body.kakaoId").type(NUMBER).description("카카오 장소 id"),
                                fieldWithPath("body.datingDay").type(ARRAY).description("데이트 날짜"),
                                fieldWithPath("body.score").type(NUMBER).description("장소에 대한 평점"),
                                fieldWithPath("body.category").type(STRING).description("장소 카테고리"),
                                fieldWithPath("body.boyText").type(STRING).description("남자친구 다이어리 내용"),
                                fieldWithPath("body.girlText").type(STRING).description("여자친구 다이어리 내용")
                        )
                ))
        ;
    }

    @DisplayName("다이어리 목록을 조회하는 API")
    @Test
    void getDiaryList() throws Exception {
        this.mockMvc.perform(
                        get("/v1/diaries")
                                .header("memberId", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diary-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("body[0].diaryId").type(NUMBER).description("다이어리 id"),
                                fieldWithPath("body[0].kakaoId").type(NUMBER).description("카카오 장소 id")
                        )
                ));
    }

    @DisplayName("다이어리를 수정하는 API")
    @Test
    void editDiary() throws Exception {

        MockDiaryEditRequest mockDiaryEditRequest =
                new MockDiaryEditRequest("1L", "new-location", 5, "2023-10-23", "ACCOMODATION", "오늘 너무 좋았어");

        this.mockMvc.perform(
                        patch("/v1/diaries/{id}", 1L)
                                .header("memberId", 1L)
                                .content(objectMapper.writeValueAsString(mockDiaryEditRequest))
                                .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diary-edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디")
                        )
                ));
    }

    @DisplayName("다이어리를 삭제하는 API")
    @Test
    void deleteDiary() throws Exception {
        this.mockMvc.perform(
                        delete("/v1/diaries/{id}", 1L)
                                .header("memberId", 1L)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("diary-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
