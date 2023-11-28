package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.couple.QCouple;
import com.lovely4k.backend.member.QMember;
import com.lovely4k.backend.question.QQuestion;
import com.lovely4k.backend.question.QQuestionChoices;
import com.lovely4k.backend.question.QQuestionForm;
import com.lovely4k.backend.question.repository.response.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.lovely4k.backend.question.QQuestion.question;
import static com.lovely4k.backend.question.QQuestionForm.questionForm;

@RequiredArgsConstructor
@Repository
public class QuestionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 1. 질문id가 questionId인 Question row에서 girlChoiceIndex, boyChoiceIndex를 얻어서
     * QuestionForm 테이블과 조인한 후 girlAnswer과 boyAnswer, questionContent를 얻어온다.
     * 2. 1에서 조회된 Question과 Couple 테이블을 couple_id로 join해서 Couple을 가져온다.
     * 3. 2에서 가져온 Couple에서 boy_id가 loginUserId 같을 경우 myChoiceIndex에 Question에서 얻어온
     * boyChoiceIndex를 할당하고 해당 값이 n이면 1에서 얻어온 QuestionForm의 n번째 선택지를 myAnswer에 할당해준다.
     * (else 조건도 동일한 식으로 진행)
     * 5. 3에서 가져온 couple row에서 boy_id, girl_id를 비교하며 상대방에 해당하는 id와 member를 조인해서 상대방의 프로필 정보를 가져온다.
     * */
    public QuestionDetailsResponse findQuestionDetails(Long questionId, Long loginUserId, String picture) {
        QCouple couple = QCouple.couple;
        QMember opponentMember = new QMember("opponentMember");

        var myChoiceIndex = getMyChoiceIndex(loginUserId, couple);
        var opponentChoiceIndex = getOpponentChoiceIndex(loginUserId, couple);

        return jpaQueryFactory
            .select(Projections.constructor(QuestionDetailsResponse.class,
                questionForm.questionContent,
                getChoiceCase(myChoiceIndex, questionForm.questionChoices),
                getChoiceCase(opponentChoiceIndex, questionForm.questionChoices),
                myChoiceIndex,
                opponentChoiceIndex,
                ConstantImpl.create(picture),
                opponentMember.imageUrl
            ))
            .from(question)
            .innerJoin(question.questionForm, questionForm)
            .innerJoin(couple).on(question.coupleId.eq(couple.id))
            .innerJoin(opponentMember).on(
                couple.boyId.eq(loginUserId).and(couple.girlId.eq(opponentMember.id))
                    .or(couple.girlId.eq(loginUserId).and(couple.boyId.eq(opponentMember.id)))
            )
            .where(question.id.eq(questionId))
            .fetchOne();
    }

    private StringExpression getChoiceCase(NumberExpression<Integer> choiceIndex, QQuestionChoices questionChoices) {
        return new CaseBuilder()
            .when(choiceIndex.eq(1)).then(questionChoices.firstChoice)
            .when(choiceIndex.eq(2)).then(questionChoices.secondChoice)
            .when(choiceIndex.eq(3)).then(questionChoices.thirdChoice)
            .when(choiceIndex.eq(4)).then(questionChoices.fourthChoice)
            .otherwise("");
    }

    private NumberExpression<Integer> getMyChoiceIndex(Long memberId, QCouple couple) {
        return new CaseBuilder()
            .when(couple.boyId.eq(memberId)).then(question.boyChoiceIndex)
            .otherwise(question.girlChoiceIndex);
    }

    private NumberExpression<Integer> getOpponentChoiceIndex(Long memberId, QCouple couple) {
        return new CaseBuilder()
            .when(couple.boyId.eq(memberId)).then(question.girlChoiceIndex)
            .otherwise(question.boyChoiceIndex);
    }

    public AnsweredQuestionResponse findAnsweredQuestions(Long id, Long coupleId, Integer limit) {
        QQuestion question = QQuestion.question;

        BooleanBuilder whereClause = new BooleanBuilder()
            .and(question.coupleId.eq(coupleId))
            .and(question.boyChoiceIndex.ne(0))
            .and(question.girlChoiceIndex.ne(0));

        if (id != null) whereClause.and(question.id.lt(id));

        List<QuestionResponse> result = jpaQueryFactory
            .select(Projections.constructor(QuestionResponse.class,
                question.id,
                question.questionForm.questionContent))
            .from(question)
            .where(whereClause)
            .orderBy(question.id.desc())
            .limit(limit)
            .fetch();

        return new AnsweredQuestionResponse(result);
    }

    //오늘의 질문 조회. 커스텀 질문, 서버에서 주는 질문 두개 조회돌 경우 커스텀 질문으로 응답.
    public DailyQuestionResponse findDailyQuestion(Long coupleId) {
        QQuestion question = QQuestion.question;
        QCouple couple = QCouple.couple;
        QQuestionForm questionForm = QQuestionForm.questionForm;

        NumberExpression<Long> questionDayExpression = Expressions.numberTemplate(
            Long.class,
            "DATEDIFF(CURRENT_DATE, {0})",
            couple.createdDate);


        return jpaQueryFactory
            .select(Projections.constructor(DailyQuestionResponse.class,
                question.id,
                questionForm.questionContent,
                questionForm.questionChoices.firstChoice,
                questionForm.questionChoices.secondChoice,
                questionForm.questionChoices.thirdChoice,
                questionForm.questionChoices.fourthChoice,
                questionForm.questionFormType))
            .from(question)
            .join(question.questionForm, questionForm)
            .join(couple).on(question.coupleId.eq(couple.id))
            .where(question.coupleId.eq(coupleId)
                .and(question.questionDay.eq(questionDayExpression)))
            .orderBy(question.id.desc())
            .fetchFirst();
    }


    public Optional<QuestionGameResponse> findQuestionGame(Long coupleId, Long loginUserId) {
        QCouple couple = QCouple.couple;
        var opponentChoiceIndex = getOpponentChoiceIndex(loginUserId, couple);

        return Optional.ofNullable(jpaQueryFactory.select(Projections.constructor(
                QuestionGameResponse.class,
                questionForm.questionContent,
                questionForm.questionChoices.firstChoice,
                questionForm.questionChoices.secondChoice,
                questionForm.questionChoices.thirdChoice,
                questionForm.questionChoices.fourthChoice,
                opponentChoiceIndex
            )).from(question)
            .innerJoin(question.questionForm, questionForm)
            .innerJoin(couple).on(question.coupleId.eq(coupleId))
            .where(
                question.coupleId.eq(coupleId),
                question.boyChoiceIndex.ne(0),
                question.girlChoiceIndex.ne(0)
            )
            .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
            .fetchFirst());
    }
}