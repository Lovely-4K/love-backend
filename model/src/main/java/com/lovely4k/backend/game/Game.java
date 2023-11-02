package com.lovely4k.backend.game;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Game extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "couple_id")
    private Long coupleId;

    @Column(name = "question_id")
    private Long questionId;

    @Column(length = 15, name = "boy_answer_status")
    @Enumerated(EnumType.STRING)
    private AnswerStatus boyAnswerStatus;

    @Column(length = 15, name = "girl_answer_status")
    @Enumerated(EnumType.STRING)
    private AnswerStatus girlAnswerStatus;

    @Column(name = "penalty")
    private String penalty;
}
