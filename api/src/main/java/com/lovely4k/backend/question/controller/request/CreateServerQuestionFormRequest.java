package com.lovely4k.backend.question.controller.request;

import com.lovely4k.backend.question.service.request.CreateServerQuestionFormServiceRequest;
import jakarta.validation.constraints.NotBlank;

public record CreateServerQuestionFormRequest(
    @NotBlank(message = "질문 내용은 비어 있을 수 없습니다.")
    String questionContent,

    @NotBlank(message = "첫 번째 선택지는 비어 있을 수 없습니다.")
    String firstChoice,

    @NotBlank(message = "두 번째 선택지는 비어 있을 수 없습니다.")
    String secondChoice,

    String thirdChoice,
    String fourthChoice,
    Long questionDay
) {
    public CreateServerQuestionFormServiceRequest toServiceRequest() {
        return new CreateServerQuestionFormServiceRequest(questionContent, firstChoice, secondChoice, thirdChoice, fourthChoice, questionDay);
    }
}
