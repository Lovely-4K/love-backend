package com.lovely4k.backend.question;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import com.lovely4k.backend.member.Sex;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "couple_id")
    private Long coupleId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "question_form_id")
    private QuestionForm questionForm;

    @Column(name = "boy_choice_index")
    private int boyChoiceIndex;  // 1, 2, 3, or 4

    @Column(name = "girl_choice_index")
    private int girlChoiceIndex;

    @Column(name = "question_day")
    private long questionDay;

    @Version
    private Long version;

    private Question(Long coupleId, QuestionForm questionForm, Long questionDay) {
        this.coupleId = Objects.requireNonNull(coupleId, "coupleId는 null값이 될 수 없습니다.");
        this.questionForm = Objects.requireNonNull(questionForm, "questionForm은 null 값이 될 수 없습니다.");
        this.questionDay = Objects.requireNonNull(questionDay, "questionDay는 null 값이 될 수 없습니다.");
    }

    public static Question create(Long coupleId, QuestionForm questionForm, Long questionDay) {
        return new Question(coupleId, questionForm, questionDay);
    }

    public void validateAnswer() {
        validateAnswer(boyChoiceIndex);
        validateAnswer(girlChoiceIndex);
    }

    private void validateAnswer(int answer) {
        if (answer == 0) {
            throw new IllegalStateException("질문에 답변을 아직 안했습니다.");
        }
    }

    public void updateAnswer(int answer, Sex sex) {
        validateChoice(answer);
        switch (sex) {  // NOSONAR
            case MALE -> this.boyChoiceIndex = answer;
            case FEMALE -> this.girlChoiceIndex = answer;
        }
    }

    private void validateChoice(int answer) {
        switch (answer) {
            case 1 -> validateSpecificChoice(1, getQuestionChoices().getFirstChoice());
            case 2 -> validateSpecificChoice(2, getQuestionChoices().getSecondChoice());
            case 3 -> validateSpecificChoice(3, getQuestionChoices().getThirdChoice());
            case 4 -> validateSpecificChoice(4, getQuestionChoices().getFourthChoice());
            default -> throw new IllegalArgumentException("유효하지 않은 선택입니다.");
        }
    }

    private void validateSpecificChoice(int choiceIndex, String choice) {
        if (StringUtils.isEmpty(choice)) {
            throw new IllegalArgumentException("유효하지 않은 선택입니다.: " + choiceIndex);
        }
    }

    private QuestionChoices getQuestionChoices() {
        return this.questionForm.getQuestionChoices();
    }

}