package com.lovely4k.backend.question.controller.request;

import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import jakarta.validation.constraints.NotBlank;

public record CreateQuestionFormRequest(
        @NotBlank(message = "질문 내용은 비어 있을 수 없습니다.")
        String questionContent,

        @NotBlank(message = "첫 번째 선택지는 비어 있을 수 없습니다.")
        String firstChoice,

        @NotBlank(message = "두 번째 선택지는 비어 있을 수 없습니다.")
        String secondChoice,

        String thirdChoice,
        String fourthChoice
) {

    public CreateQuestionFormServiceRequest toServiceDto() {

        return new CreateQuestionFormServiceRequest(
                this.questionContent,
                this.firstChoice,
                this.secondChoice,
                this.thirdChoice,
                this.fourthChoice
        );
    }
}