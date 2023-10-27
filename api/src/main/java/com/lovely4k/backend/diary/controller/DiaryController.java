package com.lovely4k.backend.diary.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.diary.controller.request.DiaryEditRequest;
import com.lovely4k.backend.diary.controller.request.WebDiaryCreateRequest;
import com.lovely4k.backend.diary.service.DiaryService;
import com.lovely4k.backend.diary.service.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.DiaryListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createDiary(
            @RequestPart(value = "images", required = false) List<MultipartFile> multipartFileList,
            @RequestPart(value = "texts") @Valid WebDiaryCreateRequest request,
            @RequestParam Long memberId
    ) {
        Long diaryId = diaryService.createDiary(multipartFileList, request.toServiceRequest(), memberId);

        return ApiResponse.created("/v1/diaries", diaryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryDetailResponse>> getDiaryDetail(
            @PathVariable Long id,
            @RequestHeader Long memberId
    ) {

        return ApiResponse.ok(diaryService.getDiaryDetail(id, memberId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DiaryListResponse>>> getDiaryList(
            @RequestHeader Long coupleId,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "localDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.ok(diaryService.getDiaryList(coupleId, category, pageable));
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
