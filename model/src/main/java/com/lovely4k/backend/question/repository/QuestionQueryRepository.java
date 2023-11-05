package com.lovely4k.backend.question.repository;

import com.lovely4k.backend.couple.QCouple;
import com.lovely4k.backend.member.QMember;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.question.repository.response.QuestionDetailsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}