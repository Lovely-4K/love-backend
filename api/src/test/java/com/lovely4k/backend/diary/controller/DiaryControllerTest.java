package com.lovely4k.backend.diary.controller;

import com.lovely4k.backend.ControllerTestSupport;
import com.lovely4k.docs.diary.MockDiaryCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DiaryControllerTest extends ControllerTestSupport {

    @DisplayName("다이어리를 작성한다.")
    @Test
    void createDiary() throws Exception{
        // given
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 5, "2023-10-20", "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
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
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest(null, "서울 강동구 테헤란로", 5, "2023-10-20", "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }

    @DisplayName("다이어리 작성 시 location은 필수값이다.")
    @Test
    void createDiaryWithoutLocation() throws Exception {
        // given
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", null, 5, "2023-10-20", "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
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
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", -1,"2023-10-20", "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
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
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 6,"2023-10-20", "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
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
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 5, null, "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
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
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 5, "2023-1-2", "ACCOMODATION", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.body.title").value("DateTimeParseException"))
        ;
    }

    @DisplayName("다이어리 작성 시 카테고리는 CAFE, FOOD, ACCOMODATION, CULTURE, ETC 만 가능하다.")
    @Test
    void createDiaryWithInvalidCategory() throws Exception {
        // given
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 5, "2023-10-20", "LEISURE", "test Text");

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
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
        MockDiaryCreateRequest diaryCreateRequest =
                new MockDiaryCreateRequest("1", "서울 강동구 테헤란로", 5, "2023-10-20", "ACCOMODATION", null);

        // when && then
        mockMvc.perform(
                        post("/v1/diaries")
                                .content(objectMapper.writeValueAsString(diaryCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("memberId", "1")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.body.title").value("MethodArgumentNotValidException"))
        ;
    }



}
