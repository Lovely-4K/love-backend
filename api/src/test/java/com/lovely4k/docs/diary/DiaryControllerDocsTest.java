package com.lovely4k.docs.diary;

import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.diary.service.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.DiaryListResponse;
import com.lovely4k.backend.diary.service.response.PhotoListResponse;
import com.lovely4k.backend.location.Category;
import com.lovely4k.docs.RestDocsSupport;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    void createDiary() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", 5, "2023-10-20", "ACCOMODATION", "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(secondImage)
                    .file(mockMultipartFile)
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
                    responseHeaders(
                        headerWithName("Location").description("생성된 다이어리과 관련한 url")
                    ),
                    responseFields(
                        fieldWithPath("code").type(NUMBER).description("코드"),
                        fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디"),
                        fieldWithPath("links[0].rel").type(STRING).description("relation of url"),
                        fieldWithPath("links[0].href").type(STRING).description("url of relation")
                    )
                )
            )
        ;
    }

    @DisplayName("다이어리를 상세 조회하는 API")
    @Test
    void getDiaryDetail() throws Exception {
        // stubbing
        when(diaryService.findDiaryDetail(any(), any()))
            .thenReturn(
                new DiaryDetailResponse(102L,
                    LocalDate.of(2021, 10, 20), 5, Category.FOOD,
                    "여기 음식 최고", "포브스 선정 맛집 답다.",
                    PhotoListResponse.builder().firstImage("image-url1").secondImage("image-url2").build()
                ));

        this.mockMvc.perform(
                get("/v1/diaries/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("diary-detail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("diary ID")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.kakaoMapId").type(NUMBER).description("카카오 장소 id"),
                    fieldWithPath("body.datingDay").type(STRING).description("데이트 날짜"),
                    fieldWithPath("body.score").type(NUMBER).description("장소에 대한 평점"),
                    fieldWithPath("body.category").type(STRING).description("장소 카테고리"),
                    fieldWithPath("body.boyText").type(STRING).description("남자친구 다이어리 내용"),
                    fieldWithPath("body.girlText").type(STRING).description("여자친구 다이어리 내용"),
                    fieldWithPath("body.pictures.firstImage").type(STRING).optional().description("이미지 주소"),
                    fieldWithPath("body.pictures.secondImage").type(STRING).optional().description("이미지 주소"),
                    fieldWithPath("body.pictures.thirdImage").type(STRING).optional().description("이미지 주소"),
                    fieldWithPath("body.pictures.fourthImage").type(STRING).optional().description("이미지 주소"),
                    fieldWithPath("body.pictures.fifthImage").type(STRING).optional().description("이미지 주소"),
                    fieldWithPath("links[0].rel").type(STRING).description("relation of url"),
                    fieldWithPath("links[0].href").type(STRING).description("url of relation")
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

        when(diaryService.findDiaryList(any(), any(), any()))
            .thenReturn(responsePage);

        this.mockMvc.perform(
                get("/v1/diaries")
                    .queryParam("page", "0")
                    .queryParam("size", "10")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("diary-list",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("size").description("페이지 사이즈"),
                    parameterWithName("page").description("페이지 번호")
                ),
                relaxedResponseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.content[0].diaryId").description("다이어리 id"),
                    fieldWithPath("body.content[0].kakaoMapId").description("카카오 맵 id"),
                    fieldWithPath("body.content[0].imageUrl").description("이미지 주소").optional(),
                    fieldWithPath("body.pageable.pageNumber").description("페이지 번호"),
                    fieldWithPath("body.pageable.pageSize").description("페이지 사이즈"),
                    fieldWithPath("body.first").description("첫번째 페이지 여부"),
                    fieldWithPath("body.numberOfElements").description("컨텐츠 수"),
                    fieldWithPath("body.empty").description("empty 여부"),
                    fieldWithPath("links[0].rel").type(STRING).description("relation of url"),
                    fieldWithPath("links[0].href").type(STRING).description("url of relation")

                )
            ));
    }


    @Disabled("현재 미제공 API 이기 때문에 테스트를 수행하지 않습니다. ")
    @DisplayName("다이어리를 수정하는 API")
    @Test
    void editDiary() throws Exception {

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest("1L", "new-address", 5, "2023-10-23", "ACCOMODATION", "오늘 너무 좋았어");

        this.mockMvc.perform(
                patch("/v1/diaries/{id}", 1L)
                    .content(objectMapper.writeValueAsString(mockDiaryEditRequest))
                    .contentType("application/json")
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("diary-edit",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디"),
                    fieldWithPath("links[0].rel").type(STRING).description("relation of url"),
                    fieldWithPath("links[0].href").type(STRING).description("url of relation")
                )
            ));
    }

    @DisplayName("다이어리를 삭제하는 API")
    @Test
    void deleteDiary() throws Exception {
        this.mockMvc.perform(
                delete("/v1/diaries/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(document("diary-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("diary ID")
                )
            ));
    }
}