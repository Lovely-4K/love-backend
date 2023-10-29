package com.lovely4k.docs.diary;

import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.diary.service.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.DiaryListResponse;
import com.lovely4k.backend.diary.service.response.PhotoList;
import com.lovely4k.backend.location.Category;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
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
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", 5, "2023-10-20", "ACCOMODATION", "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .file(mockMultipartFile)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "5")
                                .param("datingDay", "2023-10-20")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("diary-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestPartFields("texts",
                                        fieldWithPath("kakaoMapId").type(NUMBER).description("카카오 맵 id"),
                                        fieldWithPath("address").type(STRING).description("장소에 대한 주소"),
                                        fieldWithPath("score").type(NUMBER).description("장소에 대한 평점"),
                                        fieldWithPath("datingDay").type(STRING).description("데이트 한 날짜"),
                                        fieldWithPath("category").type(STRING).description("장소 카테고리"),
                                        fieldWithPath("text").type(STRING).description("장소에 대한 일기")
                                ),
                                requestParts(
                                        partWithName("texts").ignored(),
                                        partWithName("images").description("장소에 대한 이미지").optional()
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
        // stubbing
        when(diaryService.findDiaryDetail(1L, 1L))
                .thenReturn(
                        new DiaryDetailResponse(102L,
                                LocalDate.of(2021, 10, 20), 5, Category.FOOD,
                                "여기 음식 최고", "포브스 선정 맛집 답다.",
                                PhotoList.builder().firstImage("image-url1").secondImage("image-url2").build()
                        ));

        this.mockMvc.perform(
                        get("/v1/diaries/{id}", 1L)
                                .param("coupleId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diary-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("body.kakaoMapId").type(NUMBER).description("카카오 장소 id"),
                                fieldWithPath("body.datingDay").type(ARRAY).description("데이트 날짜"),
                                fieldWithPath("body.score").type(NUMBER).description("장소에 대한 평점"),
                                fieldWithPath("body.category").type(STRING).description("장소 카테고리"),
                                fieldWithPath("body.boyText").type(STRING).description("남자친구 다이어리 내용"),
                                fieldWithPath("body.girlText").type(STRING).description("여자친구 다이어리 내용"),
                                fieldWithPath("body.pictures.firstImage").type(STRING).optional().description("이미지 주소"),
                                fieldWithPath("body.pictures.secondImage").type(STRING).optional().description("이미지 주소"),
                                fieldWithPath("body.pictures.thirdImage").type(STRING).optional().description("이미지 주소"),
                                fieldWithPath("body.pictures.fourthImage").type(STRING).optional().description("이미지 주소"),
                                fieldWithPath("body.pictures.fifthImage").type(STRING).optional().description("이미지 주소")
                        )
                ))
        ;
    }

    @DisplayName("다이어리 목록을 조회하는 API")
    @Test
    void getDiaryList() throws Exception {
        // stubbing
        List<DiaryListResponse> diaryListResponseList = List.of(
                new DiaryListResponse(3L, 103L, "image-url"),
                new DiaryListResponse(2L, 103532L, "image-url"),
                new DiaryListResponse(1L, 123562L, "image-url")
        );

        PageImpl<DiaryListResponse> responsePage =
                new PageImpl<>(
                        diaryListResponseList,
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "localDateTime")),
                        diaryListResponseList.size());

        when(diaryService.findDiaryList(eq(1L), any(), any()))
                .thenReturn(responsePage);

        this.mockMvc.perform(
                        get("/v1/diaries")
                                .header("coupleId", 1L)
                                .param("page", "0")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("diary-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("body.content[0].diaryId").description("다이어리 id"),
                                fieldWithPath("body.content[0].kakaoMapId").description("카카오 맵 id"),
                                fieldWithPath("body.content[0].imageUrl").description("이미지 주소").optional(),
                                fieldWithPath("body.pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("body.pageable.pageSize").description("페이지 사이즈"),
                                fieldWithPath("body.first").description("첫번째 페이지 여부"),
                                fieldWithPath("body.numberOfElements").description("컨텐츠 수"),
                                fieldWithPath("body.empty").description("empty 여부")

                        )
                ));
    }

    @DisplayName("다이어리를 수정하는 API")
    @Test
    void editDiary() throws Exception {

        MockDiaryEditRequest mockDiaryEditRequest =
                new MockDiaryEditRequest("1L", "new-address", 5, "2023-10-23", "ACCOMODATION", "오늘 너무 좋았어");

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
                                .queryParam("coupleId", "1")
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("diary-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
