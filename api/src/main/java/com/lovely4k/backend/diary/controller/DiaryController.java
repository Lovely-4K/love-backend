package com.lovely4k.backend.diary.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.diary.controller.request.WebDiaryCreateRequest;
import com.lovely4k.backend.diary.controller.request.DiaryEditRequest;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.diary.service.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.DiaryListResponse;
import com.lovely4k.backend.location.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;


    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createDiary(
            @RequestBody @Valid WebDiaryCreateRequest request,
            @RequestParam Long memberId
    ) {
        // 이미지 업로드 기능 추가 예정
        Long diaryId = diaryService.createDiary(request.toServiceRequest(), memberId);

        return ApiResponse.created("/v1/diaries", diaryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryDetailResponse>> getDiaryDetail(
            @PathVariable Long id,
            @RequestHeader Long memberId
    ) {
        return ApiResponse.ok(new DiaryDetailResponse(1L, LocalDate.of(2023, 10, 20), 4, Category.FOOD, "너무 좋았어..", "완전 맛집!"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiaryListResponse>>> getDiaryList(
            @RequestHeader Long memberId
    ) {
        return ApiResponse.ok(List.of(
                new DiaryListResponse(1L, 1L),
                new DiaryListResponse(2L, 102L)
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> editDiary(
            @PathVariable Long id,
            @RequestHeader Long memberId,
            @RequestBody DiaryEditRequest request
    ) {
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long id,
            @RequestHeader Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }
}
