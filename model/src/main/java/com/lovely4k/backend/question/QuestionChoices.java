package com.lovely4k.backend.question;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionChoices {

    @Column(name = "first_choice")
    private String firstChoice;

    @Column(name = "second_choice")
    private String secondChoice;

    @Column(name = "third_choice")
    private String thirdChoice;

    @Column(name = "fourth_choice")
    private String fourthChoice;

    public static QuestionChoices create(String firstChoice, String secondChoice, String thirdChoice, String fourthChoice) {
        if (StringUtils.isEmpty(firstChoice) || StringUtils.isEmpty(secondChoice)) {
            throw new IllegalArgumentException("첫 번째 선택지, 두 번째 선택지는 무조건 존재해야 합니다.");
        }

        return new QuestionChoices(firstChoice, secondChoice, thirdChoice, fourthChoice);
    }
}