package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.couple.repository.response.FindCoupleProfileResponse;
import com.lovely4k.backend.member.QMember;
import com.lovely4k.backend.member.Sex;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.lovely4k.backend.couple.QCouple.couple;

@RequiredArgsConstructor
@Repository
public class CoupleQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public FindCoupleProfileResponse findCoupleProfile(Long memberId) {


        QMember my = new QMember("my");
        QMember opponent = new QMember("opponent");

        return jpaQueryFactory
            .select(Projections.constructor(FindCoupleProfileResponse.class,
                my.nickname,
                my.mbti,
                my.imageUrl,
                my.id,
                my.calendarColor,
                my.birthday,
                opponent.nickname,
                opponent.mbti,
                opponent.imageUrl,
                opponent.id,
                opponent.birthday,
                opponent.calendarColor,
                couple.meetDay
            ))
            .from(my)
            .leftJoin(couple).on(my.coupleId.eq(couple.id))
            .leftJoin(opponent)
            .on(my.sex.eq(Sex.valueOf("MALE")).and(couple.girlId.eq(opponent.id))
                .or(my.sex.eq(Sex.valueOf("FEMALE")).and(couple.boyId.eq(opponent.id))))
            .where(my.id.eq(memberId))
            .fetchOne();
    }
}
