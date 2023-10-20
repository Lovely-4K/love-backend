package com.lovely4k.backend.question;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "couple_id")
    private Long coupleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_form_id")
    private QuestionForm questionForm;

    @Column(name = "boy_answer")
    private String boyAnswer;

    @Column(name = "girl_answer")
    private String girlAnswer;

    @Builder
    private Question(Long coupleId, QuestionForm questionForm, String boyAnswer, String girlAnswer) {
        this.coupleId = coupleId;
        this.questionForm = questionForm;
        this.boyAnswer = boyAnswer;
        this.girlAnswer = girlAnswer;
    }
}
