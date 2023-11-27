package com.lovely4k.docs.diary;

import com.lovely4k.backend.diary.controller.DiaryController;
import com.lovely4k.backend.diary.controller.request.DiaryDeleteRequest;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.diary.service.response.*;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
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
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
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
                        fieldWithPath("placeName").type(STRING).description("장소 이름"),
                        fieldWithPath("score").type(NUMBER).description("장소에 대한 평점"),
                        fieldWithPath("datingDay").type(STRING).description("데이트 한 날짜"),
                        fieldWithPath("category").type(STRING).description("장소 카테고리"),
                        fieldWithPath("latitude").type(NUMBER).description("위도"),
                        fieldWithPath("longitude").type(NUMBER).description("경도"),
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
        when(diaryService.findDiaryDetail(any(), any(), any()))
            .thenReturn(
                new DiaryDetailResponse(102L,
                    LocalDate.of(2021, 10, 20), 5, Category.FOOD,
                    "여기 음식 최고", "포브스 선정 맛집 답다.",
                    PhotoListResponse.builder().firstImage("image-url1").secondImage("image-url2").build()
                    , "하이라디오", BigDecimal.valueOf(93.887), BigDecimal.valueOf(123.986)
                ));


        this.mockMvc.perform(
                get("/v1/diaries/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
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
                    fieldWithPath("body.myText").type(STRING).description("내 다이어리 내용"),
                    fieldWithPath("body.opponentText").type(STRING).description("연인 다이어리 내용"),
                    fieldWithPath("body.pictures.firstImage").type(STRING).optional().description("이미지1 주소"),
                    fieldWithPath("body.pictures.secondImage").type(STRING).optional().description("이미지2 주소"),
                    fieldWithPath("body.pictures.thirdImage").type(STRING).optional().description("이미지3 주소"),
                    fieldWithPath("body.pictures.fourthImage").type(STRING).optional().description("이미지4 주소"),
                    fieldWithPath("body.pictures.fifthImage").type(STRING).optional().description("이미지5 주소"),
                    fieldWithPath("body.placeName").type(STRING).description("장소 이름"),
                    fieldWithPath("body.latitude").type(NUMBER).description("장소 위도"),
                    fieldWithPath("body.longitude").type(NUMBER).description("장소 경도"),
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
            new DiaryListResponse(3L, 103L, "image-url", LocalDate.of(2023, 10, 20), "starbucks", "경기도 고양시", BigDecimal.valueOf(97.1235), BigDecimal.valueOf(123.5642)),
            new DiaryListResponse(2L, 103532L, "image-url", LocalDate.of(2023, 11, 20), "cafebenne", "수원시 팔달구", BigDecimal.valueOf(98.1235), BigDecimal.valueOf(122.3300)),
            new DiaryListResponse(1L, 123562L, "image-url", LocalDate.of(2023, 12, 20), "삼시세끼", "강원도 원주시", BigDecimal.valueOf(94.1235), BigDecimal.valueOf(124.4623))
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
                    fieldWithPath("body.content[0].datingDay").description("데이트 한 날짜"),
                    fieldWithPath("body.content[0].placeName").description("데이트 장소명"),
                    fieldWithPath("body.content[0].address").description("데이트 장소 주소"),
                    fieldWithPath("body.content[0].latitude").description("데이트 장소 위도"),
                    fieldWithPath("body.content[0].longitude").description("데이트 장소 경도"),
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

    @DisplayName("Marker를 통해 다이어리 목록을 조회하는 API")
    @Test
    void getDiaryListByMarker() throws Exception {
        // stubbing
        List<DiaryMarkerResponse> diaryMarkerResponses = List.of(
            new DiaryMarkerResponse(1L, "image-url1", LocalDate.of(2020, 10, 20)),
            new DiaryMarkerResponse(2L, "image-url2", LocalDate.of(2021, 10, 20)),
            new DiaryMarkerResponse(3L, "image-url3", LocalDate.of(2022, 10, 20))
        );

        when(diaryService.findDiaryListByMarker(any(), any()))
            .thenReturn(new DiaryListByMarkerResponse(BigDecimal.valueOf(98.1234), BigDecimal.valueOf(123.1231), "starbucks", diaryMarkerResponses));

        this.mockMvc.perform(
                get("/v1/diaries/marker/{kakaoMapId}", 1L)
            ).andDo(print())
            .andExpect(status().isOk())
            .andDo(document("diary-list-marker",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("kakaoMapId").description("kakao map id of place")
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.latitude").description("데이트 장소의 위도"),
                    fieldWithPath("body.longitude").description("데이트 장소의 경도"),
                    fieldWithPath("body.placeName").description("데이트 장소의 위치명"),
                    fieldWithPath("body.diaries[0].diaryId").description("다이어리 id"),
                    fieldWithPath("body.diaries[0].imageUrl").description("다이어리 사진 url"),
                    fieldWithPath("body.diaries[0].datingDay").description("데이트 한 날짜"),
                    fieldWithPath("links[0].rel").type(STRING).description("relation of url"),
                    fieldWithPath("links[0].href").type(STRING).description("url of relation")
                )
            ));

    }

    @DisplayName("다이어리를 수정하는 API")
    @Test
    void editDiary() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(5, "2023-11-01", "ACCOMODATION", "여기 좋더라 다음에 또 올까~?", "다음달에도 꼭 오자 우리");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryEditRequest).getBytes(StandardCharsets.UTF_8));

        MockHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.multipart("/v1/diaries/{id}", 1L)
            .file(firstImage)
            .file(secondImage)
            .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
                request.setMethod("PATCH");
                return request;
            });
        this.mockMvc.perform(
                builder
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("diary-edit",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("diary ID")
                ),
                requestPartFields("texts",
                    fieldWithPath("score").type(NUMBER).description("장소에 대한 평점"),
                    fieldWithPath("datingDay").type(STRING).description("데이트 한 날짜"),
                    fieldWithPath("category").type(STRING).description("장소 카테고리"),
                    fieldWithPath("myText").type(STRING).description("나의 일기"),
                    fieldWithPath("opponentText").type(STRING).description("연인의 일기")
                ),
                requestParts(
                    partWithName("texts").ignored(),
                    partWithName("images").description("장소에 대한 이미지").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body").type(JsonFieldType.NULL).description("응답 바디"),
                    fieldWithPath("links[0].rel").type(STRING).description("relation of url"),
                    fieldWithPath("links[0].href").type(STRING).description("url of relation")
                )
            ));

    }

    @DisplayName("카카오 맵 위치 안에 존재하는 다이어리 목록 조회하는 API")
    @Test
    void getDiaryListInGrid() throws Exception {
        // stubbing
        List<DiaryGridResponse> diaryGridResponses = List.of(
            new DiaryGridResponse(1L, BigDecimal.valueOf(37.5563), BigDecimal.valueOf(126.9723), "서울역", 1L)
        );

        when(diaryService.findDiaryListInGrid(any(), any(), any(), any(), any()))
            .thenReturn(new DiaryListInGridResponse(diaryGridResponses));

        this.mockMvc.perform(
                get("/v1/diaries/location")
                    .queryParam("right_latitude", String.valueOf(BigDecimal.valueOf(38)))
                    .queryParam("right_longitude", String.valueOf(BigDecimal.valueOf(127)))
                    .queryParam("left_latitude", String.valueOf(BigDecimal.valueOf(36)))
                    .queryParam("left_longitude", String.valueOf(BigDecimal.valueOf(126)))
            ).andDo(print())
            .andExpect(status().isOk())
            .andDo(document("diary-list-grid",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("right_latitude").description("오른쪽 상위 위도"),
                    parameterWithName("right_longitude").description("오른쪽 상위 경도"),
                    parameterWithName("left_latitude").description("왼쪽 하위 위도"),
                    parameterWithName("left_longitude").description("왼쪽 하위 경도")
                ),
                relaxedResponseFields(
                    fieldWithPath("code").type(NUMBER).description("코드"),
                    fieldWithPath("body.diaries[0].kakaoMapId").type(NUMBER).description("카카오맵 id"),
                    fieldWithPath("body.diaries[0].latitude").type(NUMBER).description("위도"),
                    fieldWithPath("body.diaries[0].longitude").type(NUMBER).description("경도"),
                    fieldWithPath("body.diaries[0].placeName").type(STRING).description("장소 이름"),
                    fieldWithPath("body.diaries[0].diaryId").type(NUMBER).description("다이어리 id")
                )
            ));

    }

    @DisplayName("다이어리를 삭제하는 API")
    @Test
    void deleteDiary() throws Exception {
        this.mockMvc.perform(
                delete("/v1/diaries/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
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

    @DisplayName("다이어리를 다중 삭제하는 API")
    @Test
    void deleteDiaries() throws Exception {
        DiaryDeleteRequest request = new DiaryDeleteRequest(List.of(1L, 3L, 1000L, 922L));

        this.mockMvc.perform(
                post("/v1/diaries/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(document("diary-delete-list",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("diaryList").type(ARRAY).description("다이어리 삭제 리스트")
                )
            ));
    }
}