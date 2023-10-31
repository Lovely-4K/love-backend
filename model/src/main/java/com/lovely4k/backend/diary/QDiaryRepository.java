package com.lovely4k.backend.diary;

import com.lovely4k.backend.location.Category;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Supplier;

import static com.lovely4k.backend.diary.QDiary.diary;

@Repository
public class QDiaryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public QDiaryRepository(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<Diary> findAll(Long coupleId, Category category, Pageable pageable) {
        List<Diary> diaries = jpaQueryFactory.selectFrom(diary)
                .where(coupleIdEq(coupleId).and(categoryEq(category)))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(diary.count())
                .from(diary)
                .where(coupleIdEq(coupleId).and(categoryEq(category)))
                .fetchOne();

        return new PageImpl<>(diaries, pageable, total);
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }

    private BooleanExpression coupleIdEq(Long coupleId) {
        if (coupleId == null) {
            throw new IllegalArgumentException("couple id must not be null");
        }
        return diary.coupleId.eq(coupleId);
    }

    private BooleanBuilder categoryEq(Category category) {
        return nullSafeBuilder(() -> diary.location.category.eq(category));
    }

    private OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

}
