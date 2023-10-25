package com.lovely4k.backend.diary.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.docs.diary.MockDiaryCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DiaryControllerTest extends ControllerTestSupport {

    @DisplayName("다이어리를 작성한다.")
    @Test
    void createDiary() throws Exception{
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
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
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @DisplayName("다이어리를 작성시 이미지 파일은 필수값이 아니다.")
    @Test
    void createDiaryWithoutImage() throws Exception{
        // given

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
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
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @DisplayName("다이어리 작성 시 kakaoId는 필수 값이다.")
    @Test
    void createDiaryWithoutKakaoId() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "5")
                                .param("datingDay", "2023-10-20")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
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
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("score", "5")
                                .param("datingDay", "2023-10-20")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
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
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "-1")
                                .param("datingDay", "2023-10-20")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
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
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "6")
                                .param("datingDay", "2023-10-20")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
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
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "5")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
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
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "5")
                                .param("datingDay", "2023-1-2")
                                .param("category", "ACCOMODATION")
                                .param("text", "여기 숙소 좋았어!")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
        ;
    }

    @DisplayName("다이어리 작성 시 카테고리는 CAFE, FOOD, ACCOMODATION, CULTURE, ETC 만 가능하다.")
    @Test
    void createDiaryWithInvalidCategory() throws Exception {
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "5")
                                .param("datingDay", "2023-10-20")
                                .param("category", "LEISURE")
                                .param("text", "여기 숙소 좋았어!")
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
        // given
        MockMultipartFile firstImage = new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("images", "image2.png", "image/png", "some-image".getBytes());

        // when && then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/v1/diaries")
                                .file(firstImage)
                                .file(secondImage)
                                .param("memberId", "1")
                                .param("kakaoMapId", "1")
                                .param("address", "서울 강동구 테헤란로")
                                .param("score", "5")
                                .param("datingDay", "2023-10-20")
                                .param("category", "ACCOMODATION")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }
}
