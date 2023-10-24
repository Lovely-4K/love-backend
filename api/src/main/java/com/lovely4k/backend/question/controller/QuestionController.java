package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import com.lovely4k.backend.question.service.response.DailyQuestionResponse;
import com.lovely4k.backend.question.service.response.QuestionDetailsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/v1/questions")
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyQuestionResponse>> getDailyQuestion(@RequestParam("coupleId") Long coupleId) {
        return ApiResponse.ok(questionService.findDailyQuestion(coupleId));
    }

    @PostMapping("/question-forms")
    public ResponseEntity<ApiResponse<CreateQuestionFormResponse>> createQuestionForm(@RequestBody @Valid CreateQuestionFormRequest request, @RequestParam("userId") Long userId, @RequestParam("coupleId") Long coupleId) {
        return ApiResponse.created("/v1/questions/question-forms",
                1L,
                questionService.createQuestionForm(request.toServiceDto(), coupleId, userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateQuestionResponse>> createQuestion(@RequestParam("coupleId") Long coupleId) {
        return ApiResponse.created("/v1/questions/question-forms",
                1L,
                questionService.createQuestion(coupleId));
    }

    @PatchMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<Void>> answerQuestion(@PathVariable("id") Long id) {
        questionService.updateQuestionAnswer();
        return ApiResponse.ok();
    }

    //TODO: 우리가 답변한 질문 모아보기 페이징 처리 어케 할지

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<QuestionDetailsResponse>> getQuestionDetails(@PathVariable("id") Long id) {
        return ApiResponse.ok(questionService.findQuestionDetails(id));
    }

}