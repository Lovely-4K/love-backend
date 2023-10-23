package com.lovely4k.backend.question.controller.request;

import com.lovely4k.backend.question.service.request.CreateQuestionFormServiceRequest;
import jakarta.validation.constraints.NotBlank;

public record CreateQuestionFormRequest(
        @NotBlank
        String questionContent,

        @NotBlank
        String firstChoice,

        @NotBlank
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