package com.lovely4k.backend.question;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "couple_id")
    private Long coupleId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "question_form_id")
    private QuestionForm questionForm;

    @Column(name = "boy_answer")
    private String boyAnswer;

    @Column(name = "girl_answer")
    private String girlAnswer;

    @Column(name = "question_day")
    private long questionDay;

    @Builder
    private Question(Long coupleId, QuestionForm questionForm, String boyAnswer, String girlAnswer, long questionDay) {
        this.coupleId = coupleId;
        this.questionForm = questionForm;
        this.boyAnswer = boyAnswer;
        this.girlAnswer = girlAnswer;
        this.questionDay = questionDay;
    }

    public void validateAnswer() {
        if (!isAnswerComplete()) {
            throw new IllegalStateException("질문에 답변을 아직 안했습니다.");
        }
    }

    private boolean isAnswerComplete() {
        return !boyAnswer.isEmpty() && !girlAnswer.isEmpty();
    }

}