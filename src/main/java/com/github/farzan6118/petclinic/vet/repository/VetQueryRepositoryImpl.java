package com.github.farzan6118.petclinic.vet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VetQueryRepositoryImpl implements VetQueryRepository {

    private final JPAQueryFactory queryFactory;

//    @Override
//    public List<Vet> findAvailableVets(LocalDateTime start, LocalDateTime end) {
//
//        QVet vet = QVet.vet;
//        QVisit visit = QVisit.visit;
//        QVetAvailability vetAvailability = QVetAvailability.vetAvailability;
//
//        return queryFactory
//                .selectFrom(vet)
//                .where(
//                        // Vet has availability covering the requested time
//                        JPAExpressions
//                                .selectOne()
//                                .from(vetAvailability)
//                                .where(
//                                        vetAvailability.vet.eq(vet),
//                                        vetAvailability.timeRange.startDateTime.loe(start),
//                                        vetAvailability.timeRange.endDateTime.goe(end),
//                                        vetAvailability.entityStatus.eq(EntityStatus.ACTIVE)
//                                )
//                                .exists(),
//
//                        // Vet has no overlapping visit
//                        JPAExpressions
//                                .selectOne()
//                                .from(visit)
//                                .where(
//                                        visit.vet.eq(vet),
//                                        visit.startTime.lt(end),
//                                        visit.endTime.gt(start),
//                                        visit.entityStatus.eq(EntityStatus.ACTIVE)
//                                )
//                                .notExists()
//                )
//                .fetch();
//    }
}
