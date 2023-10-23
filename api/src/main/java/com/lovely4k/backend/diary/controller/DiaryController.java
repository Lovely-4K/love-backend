package com.lovely4k.backend.diary.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.diary.controller.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;


    @PostMapping
    public ResponseEntity<ApiResponse<String>> createDiary(
            @RequestBody @Valid DiaryCreateRequest request,
            @RequestHeader Long memberId
    ) {
        // TODO : 이미지 업로드 기능 추가
        Long createdDiaryId = 1L;

        return ApiResponse.created("/v1/diaries", createdDiaryId);
    }

}
