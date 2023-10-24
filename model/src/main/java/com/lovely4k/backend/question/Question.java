package com.lovely4k.backend.question;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

    private Question(Long coupleId, QuestionForm questionForm, Long questionDay) {
        this.coupleId = Objects.requireNonNull(coupleId, "coupleId는 null값이 될 수 없습니다.");
        this.questionForm = Objects.requireNonNull(questionForm, "questionForm은 null 값이 될 수 없습니다.");
        this.questionDay = Objects.requireNonNull(questionDay, "questionDay는 null 값이 될 수 없습니다.");
    }

    public static Question create(Long coupleId, QuestionForm questionForm, Long questionDay) {
        return new Question(coupleId, questionForm, questionDay);
    }

    public void validateAnswer() {
        if (!isAnswerComplete()) {
            throw new IllegalStateException("질문에 답변을 아직 안했습니다.");
        }
    }

    private boolean isAnswerComplete() {
        return isNotEmpty(boyAnswer) && isNotEmpty(girlAnswer);
    }

    public void updateBoyAnswer(String boyAnswer) {
        this.boyAnswer = boyAnswer;
    }

    public void updateGirlAnswer(String girlAnswer) {
        this.girlAnswer = girlAnswer;
    }

    private boolean isNotEmpty(String answer) {
        return answer != null && !answer.isEmpty();
    }
}