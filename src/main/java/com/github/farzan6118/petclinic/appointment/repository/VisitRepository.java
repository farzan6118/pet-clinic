package com.github.farzan6118.petclinic.appointment.repository;

import com.github.farzan6118.petclinic.appointment.model.Visit;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, Long>, VisitQueryRepository {

    @EntityGraph(attributePaths = {
            "vet",
            "pet",
            "pet.owner",
            "pet.species",
            "room",
    })
    Optional<Visit> findByUuid(UUID uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select v from Visit v
            where v.uuid = :uuid
            and v.status not in (VisitStatus.CANCELLED,
                                 VisitStatus.COMPLETED)
            """)
    Optional<Visit> findByUuidForUpdate(@Param("uuid") UUID uuid);

    @Query("""
            select case when count(v) > 0 then true else false end
            from Visit v
            where v.pet.uuid = :petUuid
            and v.status not in (VisitStatus.CANCELLED,
                                 VisitStatus.COMPLETED)
            and v.timeRange.startDateTime < :visitEnd
            and v.timeRange.endDateTime > :visitStart
            and (:excludedVisitUuid is null or v.uuid <> :excludedVisitUuid)
            """)
    boolean existsPetReservation(
            @Param("petUuid") UUID petUuid,
            @Param("visitStart") LocalDateTime visitStart,
            @Param("visitEnd") LocalDateTime visitEnd,
            @Param("excludedVisitUuid") UUID excludedVisitUuid
    );

    @Query("""
            select case when count(v) > 0 then true else false end
            from Visit v
            where v.vet.uuid = :vetUuid
            and v.status not in (VisitStatus.CANCELLED,
                                 VisitStatus.COMPLETED)
            and v.timeRange.startDateTime < :visitEnd
            and v.timeRange.endDateTime > :visitStart
            and (:excludedVisitUuid is null or v.uuid <> :excludedVisitUuid)
            """)
    boolean existsVetReservation(
            @Param("vetUuid") UUID vetUuid,
            @Param("visitStart") LocalDateTime visitStart,
            @Param("visitEnd") LocalDateTime visitEnd,
            @Param("excludedVisitUuid") UUID excludedVisitUuid
    );

    @Query("""
            select case when count(v) > 0 then true else false end
            from Visit v
            where v.room.uuid = :roomUuid
            and v.status not in (VisitStatus.CANCELLED,
                                 VisitStatus.COMPLETED)
            and v.timeRange.startDateTime <= :visitEnd
            and v.timeRange.endDateTime > :visitStart
            and (:excludedVisitUuid is null or v.uuid <> :excludedVisitUuid)
            """)
    boolean existsRoomReservation(
            @Param("roomUuid") UUID roomUuid,
            @Param("visitStart") LocalDateTime visitStart,
            @Param("visitEnd") LocalDateTime visitEnd,
            @Param("excludedVisitUuid") UUID excludedVisitUuid
    );

    @Query("""
            select v
            from Visit v
            where v.vet.uuid = :vetUuid
              and v.timeRange.startDateTime >= :startOfDay
              and v.timeRange.startDateTime < :endOfDay
            """)
    List<Visit> findAllVisitsByVetUuidAndStartTimeBetween(UUID vetUuid, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("""
            select v
            from Visit v
            where v.pet.uuid = :petUuid
              and v.timeRange.startDateTime >= :startOfDay
              and v.timeRange.startDateTime < :endOfDay
            """)
    List<Visit> findAllVisitsByPetUuidAndStartTimeBetween(UUID petUuid, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("""
            select v
            from Visit v
            where v.room.uuid = :roomUuid
              and v.timeRange.startDateTime >= :startOfDay
              and v.timeRange.startDateTime < :endOfDay
            """)
    List<Visit> findAllVisitsByRoomUuidAndStartTimeBetween(UUID roomUuid, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
