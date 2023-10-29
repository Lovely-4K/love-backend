package com.lovely4k.backend.member.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.service.request.MemberProfileEditServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MemberProfileEditRequest(
    @NotNull(message = "성별을 입력해주세요.")
    Sex sex,

    @NotBlank(message = "image url을 입력해주세요.")
    String imageUrl,

    @NotBlank(message = "이름을 입력해주세요.")
    String name,

    @NotBlank(message = "별명을 입력해주세요.")
    String nickname,

    @NotNull(message = "생일을 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday,

    @NotBlank(message = "MBTI를 입력해주세요.")
    String mbti,

    @NotBlank(message = "개인 색상을 입력해주세요.")
    String calendarColor
) {
    public MemberProfileEditServiceRequest toServiceRequest() {
        return new MemberProfileEditServiceRequest(
            this.sex,
            this.imageUrl,
            this.name,
            this.nickname,
            this.birthday,
            this.mbti,
            this.calendarColor
        );
    }
}
