package com.github.farzan6118.petclinic.vet.repository;

import com.github.farzan6118.petclinic.vet.model.Vet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VetRepository extends JpaRepository<Vet, Long>, VetQueryRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Vet v where v.uuid = :uuid")
    Optional<Vet> findByUuidWithLock(@Param("uuid") UUID uuid);

    Optional<Vet> findByUuid(UUID uuid);

    boolean existsByPerson_Profile_EmailAndUuidNot(String email, UUID uuid);

    boolean existsByPerson_Profile_MobileNumberAndUuidNot(String mobile, UUID uuid);

    boolean existsByPerson_Profile_MobileNumber(String mobile);

    boolean existsByPerson_Profile_Email(String email);

    @Query("""
            select va from VetAvailability va
                join fetch va.vet v
                where v.uuid = :uuid
                and (:startTime >= va.timeRange.startDateTime
                and :endTime <= va.timeRange.endDateTime)
            """
    )
    Optional<Vet> findAvailableByUuidAndTimeRange(
            UUID uuid, LocalDateTime startTime, LocalDateTime endTime);
}
