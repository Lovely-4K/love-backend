package com.lovely4k.backend.diary.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.docs.diary.MockDiaryCreateRequest;
import com.lovely4k.docs.diary.MockDiaryEditRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DiaryControllerTest extends ControllerTestSupport {

    @DisplayName("다이어리를 작성한다.")
    @Test
    void createDiary() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ZERO, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));

        // when && then
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("201"))
            .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @DisplayName("다이어리를 작성시 이미지 파일은 필수값이 아니다.")
    @Test
    void createDiaryWithoutImage() throws Exception {
        // given
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));

        // when && then
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("201"))
            .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @DisplayName("다이어리 작성 시 kakaoMapId는 필수 값이다.")
    @Test
    void createDiaryWithoutKakaoId() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(null, "서울 강동구 테헤란로", "starbucks", 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));


        // when && then
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 address는 필수값이다.")
    @Test
    void createDiaryWithoutLocation() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, null, "starbucks", 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 placeName은 필수값이다.")
    @Test
    void createDiaryWithoutPlaceName() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "경기도 고양시", null, 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 평점은 음수 값일 수 없다.")
    @Test
    void createDiaryWithMinusScore() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", -1, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 평점은 0 ~ 5점 사이값 이다.")
    @Test
    void createDiaryWithInvalidScore() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 6, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 datingDay는 필수값이다.")
    @Test
    void createDiaryWithoutDatingDay() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, null, "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 datingDay는 'yyyy-MM-dd' 형식으로 작성 되어야 한다.")
    @Test
    void createDiaryInvalidDatingDay() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, "2023-1-2", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
        ;
    }

    @DisplayName("다이어리 작성 시 카테고리는 CAFE, FOOD, ACCOMODATION, CULTURE, ETC 만 가능하다.")
    @Test
    void createDiaryWithInvalidCategory() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, "2023-10-20", "LEISURE", BigDecimal.ZERO, BigDecimal.ONE, "여기 숙소 좋았어..!");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 작성자의 text는 필수적으로 적어야 한다.")
    @Test
    void createDiaryWithEmptyText() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-files".getBytes());
        MockDiaryCreateRequest mockDiaryCreateRequest = new MockDiaryCreateRequest(1L, "서울 강동구 테헤란로", "starbucks", 5, "2023-10-20", "ACCOMODATION", BigDecimal.ZERO, BigDecimal.ONE, null);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("texts", "texts", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(mockDiaryCreateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/v1/diaries")
                    .file(firstImage)
                    .file(mockMultipartFile)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리를 수정한다.")
    @Test
    void editDiary() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(5, "2023-11-01", "ACCOMODATION", "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isOk())
        ;
    }

    @DisplayName("다이어리를 수정시 score는 음수 일 수 없다.")
    @Test
    void editDiary_MinusScore() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(-1, "2023-11-01", "ACCOMODATION", "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리를 수정시 score는 5점을 초과할 수 없다.")
    @Test
    void editDiary_ScoreOver5() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(6, "2023-11-01", "ACCOMODATION", "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리를 수정시 datingDay는 null 이어서는 안된다. ")
    @Test
    void editDiary_NullDatingDay() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(4, null, "ACCOMODATION", "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리를 수정시 datingDay는 yyyy-MM-dd 형식이어야 한다.")
    @Test
    void editDiary_InvalidDatingDay() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(4, "1490-2-2", "ACCOMODATION", "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("HttpMessageNotReadableException"))
        ;
    }

    @DisplayName("다이어리를 수정시 category는 null 이어서는 안된다. ")
    @Test
    void editDiary_NullCategory() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(4, "2023-10-20", null, "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리를 수정시 category는 주어진 타입 외에는 사용할 수 없다.")
    @Test
    void editDiary_InvalidCategory() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(4, "2023-10-20", "SPORTS", "여기 좋더라 다음에 또 올까~?");
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리를 수정시 text는 null이어서는 안된다.")
    @Test
    void editDiary_NullText() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "image-file".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "image-file".getBytes());

        MockDiaryEditRequest mockDiaryEditRequest =
            new MockDiaryEditRequest(4, "2023-10-20", "ETC", null);
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
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

}
