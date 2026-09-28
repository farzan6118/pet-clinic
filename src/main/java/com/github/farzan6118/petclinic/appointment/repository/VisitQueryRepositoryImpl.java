package com.github.farzan6118.petclinic.appointment.repository;

import com.github.farzan6118.petclinic.appointment.dto.request.VisitAdvancedSearch;
import com.github.farzan6118.petclinic.appointment.model.QVisit;
import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VisitQueryRepositoryImpl implements VisitQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<Visit> advancedSearch(VisitAdvancedSearch request, Pageable pageable) {

        QVisit visit = QVisit.visit;

        BooleanBuilder predicate = new BooleanBuilder();

        // Visit visitDateFrom time
        addLocalDateTimeRangePredicate(
                predicate,
                visit.timeRange.startDateTime,
                request.visitDateFrom(),
                request.visitDateTo()
        );

        // Visit created date
        addInstantRangePredicate(
                predicate,
                visit.createdDate,
                request.createdDateFrom(),
                request.createdDateTo()
        );

        // Vet
        addUuidPredicate(
                predicate,
                visit.vet.uuid,
                request.vetUuid()
        );

        // Pet
        addUuidPredicate(
                predicate,
                visit.pet.uuid,
                request.petUuid()
        );

        // Room
        addUuidPredicate(
                predicate,
                visit.room.uuid,
                request.roomUuid()
        );

        Long total = queryFactory
                .select(visit.count())
                .from(visit)
                .where(predicate)
                .fetchOne();

        List<Visit> content = queryFactory
                .selectFrom(visit)
                .where(predicate)
                .orderBy(getOrderSpecifier(visit, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private void addLocalDateTimeRangePredicate(
            BooleanBuilder predicate,
            DateTimeExpression<LocalDateTime> path,
            LocalDateTime from,
            LocalDateTime to) {

        if (from != null) {
            predicate.and(path.goe(from));
        }

        if (to != null) {
            predicate.and(path.lt(to));
        }
    }

    private void addInstantRangePredicate(
            BooleanBuilder predicate, DateTimeExpression<Instant> path, Instant from, Instant to) {

        if (from != null) {
            predicate.and(path.goe(from));
        }

        if (to != null) {
            predicate.and(path.lt(to));
        }
    }

    private void addUuidPredicate(BooleanBuilder predicate, SimpleExpression<UUID> path, UUID value) {

        if (value != null) {
            predicate.and(path.eq(value));
        }
    }

    private OrderSpecifier<?> getOrderSpecifier(QVisit visit, Pageable pageable) {

        Sort.Order sortOrder = pageable.getSort()
                .stream()
                .findFirst()
                .orElse(null);

        if (sortOrder == null) {
            return new OrderSpecifier<>(Order.ASC, visit.timeRange.startDateTime);
        }

        Order order = sortOrder.isAscending() ? Order.ASC : Order.DESC;

        return switch (sortOrder.getProperty()) {
            case "startTime" -> new OrderSpecifier<>(order, visit.timeRange.startDateTime);
            case "endTime" -> new OrderSpecifier<>(order, visit.timeRange.endDateTime);
            case "createdDate" -> new OrderSpecifier<>(order, visit.createdDate);
            case "lastModifiedDate" -> new OrderSpecifier<>(order, visit.lastModifiedDate);
            case "status" -> new OrderSpecifier<>(order, visit.status);
            case "visitType" -> new OrderSpecifier<>(order, visit.visitType);
            default -> new OrderSpecifier<>(Order.ASC, visit.timeRange.startDateTime);
        };
    }

    @Override
    public List<Visit> findOverlappingVisits(
            UUID vetUuid,
            LocalDateTime start,
            LocalDateTime end) {

        QVisit visit = QVisit.visit;

        return queryFactory
                .selectFrom(visit)
                .where(
                        visit.vet.uuid.eq(vetUuid),
                        visit.timeRange.startDateTime.lt(end),
                        visit.timeRange.endDateTime.gt(start)
                )
                .orderBy(visit.timeRange.startDateTime.asc())
                .fetch();
    }
}
