package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.couple.QCouple;
import com.lovely4k.backend.member.QMember;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.QQuestion;
import com.lovely4k.backend.question.QQuestionForm;
import com.lovely4k.backend.question.repository.response.*;
import com.lovely4k.backend.question.repository.response.AnsweredQuestionResponse;
import com.lovely4k.backend.question.repository.response.DailyQuestionResponse;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.lovely4k.backend.question.repository.response.QuestionResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.lovely4k.backend.couple.QCouple.couple;
import static com.lovely4k.backend.question.QQuestion.question;
import static com.lovely4k.backend.question.QQuestionForm.questionForm;

@RequiredArgsConstructor
@Repository
public class QuestionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public QuestionDetailsResponse findQuestionDetails(long questionId, Sex sex, long memberId) {
        QMember memberBoy = new QMember("memberBoy");
        QMember memberGirl = new QMember("memberGirl");

        var myChoiceIndex = getMyChoiceIndex(sex);
        var opponentChoiceIndex = getOpponentChoiceIndex(sex);
        var myProfile = getMyProfile(sex, memberBoy, memberGirl);
        var opponentProfile = getOpponentProfile(sex, memberBoy, memberGirl);
        var memberFilter = getMemberFilter(sex, memberId, couple);

        return jpaQueryFactory
            .select(Projections.constructor(QuestionDetailsResponse.class,
                questionForm.questionContent,
                new CaseBuilder()
                    .when(myChoiceIndex.eq(1)).then(questionForm.questionChoices.firstChoice)
                    .when(myChoiceIndex.eq(2)).then(questionForm.questionChoices.secondChoice)
                    .when(myChoiceIndex.eq(3)).then(questionForm.questionChoices.thirdChoice)
                    .when(myChoiceIndex.eq(4)).then(questionForm.questionChoices.fourthChoice)
                    .otherwise(""),
                new CaseBuilder()
                    .when(opponentChoiceIndex.eq(1)).then(questionForm.questionChoices.firstChoice)
                    .when(opponentChoiceIndex.eq(2)).then(questionForm.questionChoices.secondChoice)
                    .when(opponentChoiceIndex.eq(3)).then(questionForm.questionChoices.thirdChoice)
                    .when(opponentChoiceIndex.eq(4)).then(questionForm.questionChoices.fourthChoice)
                    .otherwise(""),
                myChoiceIndex,
                opponentChoiceIndex,
                myProfile,
                opponentProfile
            ))
            .from(question)
            .join(question.questionForm, questionForm)
            .join(couple).on(question.coupleId.eq(couple.id))
            .leftJoin(memberBoy).on(couple.boyId.eq(memberBoy.id))
            .leftJoin(memberGirl).on(couple.girlId.eq(memberGirl.id))
            .where(question.id.eq(questionId).and(memberFilter))
            .fetchOne();
    }

    private NumberExpression<Integer> getMyChoiceIndex(Sex sex) {
        return sex == Sex.MALE ? question.boyChoiceIndex : question.girlChoiceIndex;
    }

    private NumberExpression<Integer> getOpponentChoiceIndex(Sex sex) {
        return sex == Sex.MALE ? question.girlChoiceIndex : question.boyChoiceIndex;
    }

    private StringExpression getMyProfile(Sex sex, QMember memberBoy, QMember memberGirl) {
        return sex == Sex.MALE ? memberBoy.imageUrl : memberGirl.imageUrl;
    }

    private StringExpression getOpponentProfile(Sex sex, QMember memberBoy, QMember memberGirl) {
        return sex == Sex.MALE ? memberGirl.imageUrl : memberBoy.imageUrl;
    }

    private BooleanExpression getMemberFilter(Sex sex, long memberId, QCouple couple) {
        return sex == Sex.MALE ? couple.boyId.eq(memberId) : couple.girlId.eq(memberId);
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
            "TIMESTAMPDIFF(DAY, {0}, CURRENT_DATE)",
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


    public Optional<QuestionGameResponse> findQuestionGame(Long coupleId, Sex sex) {

        var opponentChoiceIndex = getOpponentChoiceIndex(sex);

        return Optional.ofNullable(jpaQueryFactory.select(Projections.constructor(
                QuestionGameResponse.class,
                questionForm.questionContent,
                questionForm.questionChoices.firstChoice,
                questionForm.questionChoices.secondChoice,
                questionForm.questionChoices.thirdChoice,
                questionForm.questionChoices.fourthChoice,
                opponentChoiceIndex
            )).from(question)
            .join(question.questionForm, questionForm)
            .where(
                question.coupleId.eq(coupleId),
                question.boyChoiceIndex.ne(0),
                question.girlChoiceIndex.ne(0)
            )
            .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
            .fetchFirst());
    }
}