package com.lovely4k.backend.question.controller;

import com.lovely4k.backend.common.ApiResponse;
import com.lovely4k.backend.common.sessionuser.LoginUser;
import com.lovely4k.backend.common.sessionuser.SessionUser;
import com.lovely4k.backend.question.controller.request.AnswerQuestionRequest;
import com.lovely4k.backend.question.controller.request.AnsweredQuestionParamRequest;
import com.lovely4k.backend.question.controller.request.CreateQuestionFormRequest;
import com.lovely4k.backend.question.controller.request.CreateServerQuestionFormRequest;
import com.lovely4k.backend.question.repository.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.repository.response.DailyQuestionResponse;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.lovely4k.backend.question.repository.response.QuestionGameResponse;
import com.lovely4k.backend.question.service.QuestionQueryService;
import com.lovely4k.backend.question.service.QuestionService;
import com.lovely4k.backend.question.service.response.CreateQuestionFormResponse;
import com.lovely4k.backend.question.service.response.CreateQuestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RequestMapping(value = "/v1/questions", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionQueryService questionQueryService;

    private static final String CREATE_QUESTION_FORM = "createQuestionForm";
    private static final String GET_DAILY_QUESTION = "getDailyQuestion";
    private static final String CREATE_QUESTION = "createQuestion";
    private static final String ANSWER_QUESTION = "answerQuestion";
    private static final String GET_ANSWERED_QUESTIONS = "getAnsweredQuestions";
    private static final String GET_QUESTION_DETAILS = "getQuestionDetails";

    @SneakyThrows
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyQuestionResponse>> getDailyQuestion(@LoginUser SessionUser user) {
        return ApiResponse.ok(questionQueryService.findDailyQuestion(user.coupleId()),
            linkTo(methodOn(getClass()).getDailyQuestion(user)).withSelfRel(),
            linkTo(getClass().getMethod(CREATE_QUESTION, SessionUser.class)).withRel(CREATE_QUESTION),
            linkTo(getClass().getMethod(CREATE_QUESTION_FORM, CreateQuestionFormRequest.class, SessionUser.class)).withRel(CREATE_QUESTION_FORM));
    }

    @SneakyThrows
    @PostMapping(path = "/question-forms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreateQuestionFormResponse>> createQuestionForm(@RequestBody @Valid CreateQuestionFormRequest request, @LoginUser SessionUser user) {
        CreateQuestionFormResponse response = questionService.createQuestionForm(request.toServiceDto(), user.coupleId(), user.memberId());

        return ApiResponse.created(response, response.questionId(),
            linkTo(methodOn(getClass()).createQuestionForm(request, user)).withSelfRel(),
            linkTo(getClass().getMethod(ANSWER_QUESTION, Long.class, SessionUser.class, AnswerQuestionRequest.class)).withRel(ANSWER_QUESTION),
            linkTo(getClass().getMethod(GET_DAILY_QUESTION, SessionUser.class)).withRel(GET_DAILY_QUESTION));
    }


    @SneakyThrows
    @PostMapping
    public ResponseEntity<ApiResponse<CreateQuestionResponse>> createQuestion(@LoginUser SessionUser user) {
        CreateQuestionResponse response = questionService.createQuestion(user.coupleId());

        return ApiResponse.created(response, response.questionId(),
            linkTo(methodOn(getClass()).createQuestion(user)).withSelfRel(),
            linkTo(getClass().getMethod(GET_DAILY_QUESTION, SessionUser.class)).withRel(GET_DAILY_QUESTION));
    }


    @SneakyThrows
    @PatchMapping(path = "/{id}/answers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> answerQuestion(@PathVariable("id") Long id, @LoginUser SessionUser user, @RequestBody @Valid AnswerQuestionRequest request) {
        questionService.updateQuestionAnswer(id, user.coupleId(), user.memberId(), request.choiceNumber());

        return ApiResponse.ok(
            linkTo(methodOn(getClass()).answerQuestion(id, user, request)).withSelfRel(),
            linkTo(getClass().getMethod(GET_ANSWERED_QUESTIONS, AnsweredQuestionParamRequest.class, SessionUser.class)).withRel(GET_ANSWERED_QUESTIONS),
            linkTo(getClass().getMethod(GET_QUESTION_DETAILS, Long.class, SessionUser.class)).withRel(GET_QUESTION_DETAILS));
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ApiResponse<AnsweredQuestionResponse>> getAnsweredQuestions(@ModelAttribute @Valid AnsweredQuestionParamRequest params, @LoginUser SessionUser sessionUser) {
        return ApiResponse.ok(questionQueryService.findAllAnsweredQuestionByCoupleId(params.getId(), sessionUser.coupleId(), params.getLimit()),
            linkTo(methodOn(getClass()).getAnsweredQuestions(params, sessionUser)).withSelfRel(),
            linkTo(getClass().getMethod(GET_QUESTION_DETAILS, Long.class, SessionUser.class)).withRel(GET_QUESTION_DETAILS));
    }


    @SneakyThrows
    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<QuestionDetailsResponse>> getQuestionDetails(@PathVariable("id") Long id, @LoginUser SessionUser user) {
        return ApiResponse.ok(questionQueryService.findQuestionDetails(id, user.memberId(), user.picture()),
            linkTo(methodOn(getClass()).getQuestionDetails(id, user)).withSelfRel(),
            linkTo(getClass().getMethod(GET_ANSWERED_QUESTIONS, AnsweredQuestionParamRequest.class, SessionUser.class)).withRel(GET_ANSWERED_QUESTIONS));
    }

    @GetMapping("/games")
    public ResponseEntity<ApiResponse<QuestionGameResponse>> getQuestionGame(@LoginUser SessionUser user) {
        return ApiResponse.ok(questionQueryService.findQuestionGame(user.coupleId(), user.memberId()),
            linkTo(methodOn(getClass()).getQuestionGame(user)).withSelfRel());
    }

    //관리자용 엔드포인트
    @DeleteMapping
    public ResponseEntity<Void> init() {
        questionService.deleteQuestion();
        return ResponseEntity.noContent().build();
    }

    //관리자용 엔드포인트
    @PostMapping("/server")
    public ResponseEntity<ApiResponse<Void>> addServerQuest(@RequestBody @Valid CreateServerQuestionFormRequest request) {
        return ApiResponse.created(questionService.createServerQuestion(request.toServiceRequest()));
    }
}