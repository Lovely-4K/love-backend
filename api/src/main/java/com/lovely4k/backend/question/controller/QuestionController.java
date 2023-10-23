package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import com.lovely4k.backend.question.service.response.DailyQuestionResponse;
import com.lovely4k.backend.question.service.response.QuestionDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/v1/questions")
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyQuestionResponse>> getDailyQuestion(@RequestParam("userId") Long userId) {
        return ApiResponse.ok(new DailyQuestionResponse(1L,
                "테스트",
                List.of(new DailyQuestionResponse.QuestionChoiceResponse("선택지1"))));
    }

    @PostMapping("/question-forms")
    public ResponseEntity<ApiResponse<CreateQuestionFormResponse>> createQuestionForm(@RequestBody CreateQuestionFormRequest request, @RequestParam("userId") Long userId, @RequestParam("coupleId") Long coupleId) {
        return ApiResponse.created("/v1/questions/question-forms", 1L,new CreateQuestionFormResponse(1L,
                "테스트",
                List.of(new CreateQuestionFormResponse.QuestionChoiceResponse("선택지 1"))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateQuestionResponse>> createQuestion(@RequestParam("coupleId") Long coupleId) {
        return ApiResponse.created("/v1/questions/question-forms", 1L,
                new CreateQuestionResponse(1L,
                "테스트",
                List.of(new CreateQuestionResponse.QuestionChoiceResponse("선택지1"))));
    }

    @PatchMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<Void>> answerQuestion(@PathVariable("id") Long id) {
        return ApiResponse.ok();
    }

    //TODO: 우리가 답변한 질문 모아보기 페이징 처리 어케 할지

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<QuestionDetailsResponse>> getQuestionDetails(@PathVariable("id") Long id) {
        return ApiResponse.ok(new QuestionDetailsResponse("테스트 질문 내용", "답변 1", "답변 2"));
    }

}