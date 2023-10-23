package com.lovely4k.backend.question;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class QuestionForm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "question_content")
    private String questionContent;

    @Embedded
    private QuestionChoices questionChoices;

    @Column(name = "question_day")
    private Long questionDay;

    private QuestionForm(Long memberId, String questionContent, QuestionChoices questionChoices, Long questionDay) {
        this.memberId = memberId;
        this.questionContent = Objects.requireNonNull(questionContent);
        this.questionChoices = Objects.requireNonNull(questionChoices);
        this.questionDay = Objects.requireNonNull(questionDay);
    }

    public static QuestionForm create(Long memberId, String questionContent, QuestionChoices questionChoices, Long questionDay) {
        return new QuestionForm(memberId, questionContent, questionChoices, questionDay);

    }

}