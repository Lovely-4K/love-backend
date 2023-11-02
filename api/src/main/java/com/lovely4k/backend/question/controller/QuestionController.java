package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.controller.request.AnswerQuestionRequest;
import com.lovely4k.backend.question.controller.request.AnsweredQuestionParamRequest;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.backend.question.service.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RequestMapping(value = "/v1/questions", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionService questionService;

    private static final String CREATE_QUESTION_FORM = "createQuestionForm";
    private static final String GET_DAILY_QUESTION = "getDailyQuestion";
    private static final String CREATE_QUESTION = "createQuestion";
    private static final String ANSWER_QUESTION = "answerQuestion";
    private static final String GET_ANSWERED_QUESTIONS = "getAnsweredQuestions";
    private static final String GET_QUESTION_DETAILS = "getQuestionDetails";



    @SneakyThrows
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyQuestionResponse>> getDailyQuestion(@RequestParam("coupleId") Long coupleId) {

        return ApiResponse.ok(questionService.findDailyQuestion(coupleId),
                linkTo(methodOn(getClass()).getDailyQuestion(coupleId)).withSelfRel(),
                linkTo(getClass().getMethod(CREATE_QUESTION, Long.class)).withRel(CREATE_QUESTION),
                linkTo(getClass().getMethod(CREATE_QUESTION_FORM, CreateQuestionFormRequest.class, Long.class, Long.class)).withRel(CREATE_QUESTION_FORM));
    }

    @SneakyThrows
    @PostMapping(path = "/question-forms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreateQuestionFormResponse>> createQuestionForm(@RequestBody @Valid CreateQuestionFormRequest request, @RequestParam("memberId") Long memberId, @RequestParam("coupleId") Long coupleId) {
        CreateQuestionFormResponse response = questionService.createQuestionForm(request.toServiceDto(), coupleId, memberId);

        return ApiResponse.created(response, response.questionId(),
                linkTo(methodOn(getClass()).createQuestionForm(request, memberId, coupleId)).withSelfRel(),
                linkTo(getClass().getMethod(ANSWER_QUESTION, Long.class, Sex.class, AnswerQuestionRequest.class)).withRel(ANSWER_QUESTION),
                linkTo(getClass().getMethod(GET_DAILY_QUESTION, Long.class)).withRel(GET_DAILY_QUESTION));
    }


    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreateQuestionResponse>> createQuestion(@RequestParam("coupleId") Long coupleId) {
        CreateQuestionResponse response = questionService.createQuestion(coupleId);

        return ApiResponse.created(response, response.questionId(),
                linkTo(methodOn(getClass()).createQuestion(coupleId)).withSelfRel(),
                linkTo(getClass().getMethod(GET_DAILY_QUESTION, Long.class)).withRel(GET_DAILY_QUESTION));
    }


    @SneakyThrows
    @PatchMapping(path = "/{id}/answers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> answerQuestion(@PathVariable("id") Long id, @RequestParam("sex") Sex sex, @RequestBody @Valid AnswerQuestionRequest request) {
        questionService.updateQuestionAnswer(id, sex, request.choiceNumber());

        return ApiResponse.ok(
                linkTo(methodOn(getClass()).answerQuestion(id, sex, request)).withSelfRel(),
                linkTo(getClass().getMethod(GET_ANSWERED_QUESTIONS, AnsweredQuestionParamRequest.class)).withRel(GET_ANSWERED_QUESTIONS),
                linkTo(getClass().getMethod(GET_QUESTION_DETAILS, Long.class)).withRel(GET_QUESTION_DETAILS));
    }


    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<AnsweredQuestionResponse>> getAnsweredQuestions(@ModelAttribute @Valid AnsweredQuestionParamRequest params) {
        return ApiResponse.ok(questionService.findAllAnsweredQuestionByCoupleId(params.getId(), params.getCoupleId(), params.getLimit()),
                linkTo(methodOn(getClass()).getAnsweredQuestions(params)).withSelfRel(),
                linkTo(getClass().getMethod(GET_QUESTION_DETAILS, Long.class)).withRel(GET_QUESTION_DETAILS));
    }


    @SneakyThrows
    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<QuestionDetailsResponse>> getQuestionDetails(@PathVariable("id") Long id) {
        return ApiResponse.ok(questionService.findQuestionDetails(id),
                linkTo(methodOn(getClass()).getQuestionDetails(id)).withSelfRel(),
                linkTo(getClass().getMethod(GET_ANSWERED_QUESTIONS, AnsweredQuestionParamRequest.class)).withRel(GET_ANSWERED_QUESTIONS));
    }

}