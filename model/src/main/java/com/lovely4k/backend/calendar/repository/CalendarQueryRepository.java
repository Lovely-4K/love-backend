package com.lovely4k.backend.calendar.repository;

import com.lovely4k.backend.calendar.repository.response.FindCalendarsWithDateResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import com.lovely4k.backend.member.QMember;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.lovely4k.backend.calendar.QCalendar.calendar;
import static com.lovely4k.backend.couple.QCouple.couple;

@RequiredArgsConstructor
@Repository
public class CalendarQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<FindRecentCalendarsResponse> findRecentCalendarsWithColors(long coupleId, int limit, Long loginUserId) {
        QMember boy = new QMember("boy");
        QMember girl = new QMember("girl");

        var myId = myId(boy, girl, loginUserId);
        var opponentId = opponentId(boy, girl, loginUserId);
        var myCalendarColor = myCalendarColor(boy, girl, loginUserId);
        var opponentCalendarColor = opponentCalendarColor(boy, girl, loginUserId);

        return jpaQueryFactory
            .select(Projections.constructor(FindRecentCalendarsResponse.class,
                calendar.id,
                myId, myCalendarColor,
                opponentId, opponentCalendarColor,
                calendar.startDate, calendar.endDate,
                calendar.scheduleDetails, calendar.scheduleType, calendar.ownerId))
            .from(calendar)
            .join(couple).on(calendar.coupleId.eq(couple.id))
            .join(boy).on(couple.boyId.eq(boy.id))
            .join(girl).on(couple.girlId.eq(girl.id))
            .where(calendar.coupleId.eq(coupleId), calendar.startDate.goe(LocalDate.now()))
            .orderBy(calendar.startDate.asc())
            .limit(limit)
            .fetch();
    }

    private Expression<Long> myId(QMember boy, QMember girl, Long loginUserId) {
        return new CaseBuilder()
            .when(boy.id.eq(loginUserId)).then(boy.id)
            .otherwise(girl.id);
    }

    private Expression<String> myCalendarColor(QMember boy, QMember girl, Long loginUserId) {
       return new CaseBuilder()
            .when(boy.id.eq(loginUserId)).then(boy.calendarColor)
            .otherwise(girl.calendarColor);
    }

    private Expression<Long> opponentId(QMember boy, QMember girl, Long loginUserId) {
        return new CaseBuilder()
            .when(boy.id.eq(loginUserId)).then(girl.id)
            .otherwise(boy.id);
    }

    private Expression<String> opponentCalendarColor(QMember boy, QMember girl, Long loginUserId) {
        return new CaseBuilder()
            .when(boy.id.eq(loginUserId)).then(girl.calendarColor)
            .otherwise(boy.calendarColor);
    }

    public List<FindCalendarsWithDateResponse> findCalendarsWithDate(FindCalendarsWithDateRepositoryRequest request, Long coupleId, Long loginUserId) {
        QMember boy = new QMember("boy");
        QMember girl = new QMember("girl");

        var myId = myId(boy, girl, loginUserId);
        var opponentId = opponentId(boy, girl, loginUserId);
        var myCalendarColor = myCalendarColor(boy, girl, loginUserId);
        var opponentCalendarColor = opponentCalendarColor(boy, girl, loginUserId);

        return jpaQueryFactory
            .select(Projections.constructor(FindCalendarsWithDateResponse.class,
                calendar.id,
                myId, myCalendarColor,
                opponentId, opponentCalendarColor,
                calendar.startDate, calendar.endDate,
                calendar.scheduleDetails, calendar.scheduleType, calendar.ownerId))
            .from(calendar)
            .join(couple).on(calendar.coupleId.eq(couple.id))
            .join(boy).on(couple.boyId.eq(boy.id))
            .join(girl).on(couple.girlId.eq(girl.id))
            .where(
                calendar.coupleId.eq(coupleId),
                calendar.startDate.between(request.from(), request.to()))
            .orderBy(calendar.id.asc())
            .fetch();
    }
}