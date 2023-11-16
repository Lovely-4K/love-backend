package com.lovely4k.backend.calendar.repository;

import com.lovely4k.backend.calendar.repository.response.FindCalendarsWithDateResponse;
import com.lovely4k.backend.calendar.repository.response.FindRecentCalendarsResponse;
import com.lovely4k.backend.member.QMember;
import com.querydsl.core.types.Projections;
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

    public List<FindRecentCalendarsResponse> findRecentCalendarsWithColors(long coupleId, int limit) {
        QMember boy = new QMember("boy");
        QMember girl = new QMember("girl");

        return jpaQueryFactory
            .select(Projections.constructor(FindRecentCalendarsResponse.class,
                calendar.id,
                boy.id, boy.calendarColor,
                girl.id, girl.calendarColor,
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

    public List<FindCalendarsWithDateResponse> findCalendarsWithDate(FindCalendarsWithDateRepositoryRequest request, Long coupleId) {
        QMember boy = new QMember("boy");
        QMember girl = new QMember("girl");

        return jpaQueryFactory
            .select(Projections.constructor(FindCalendarsWithDateResponse.class,
                calendar.id,
                boy.id, boy.calendarColor,
                girl.id, girl.calendarColor,
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