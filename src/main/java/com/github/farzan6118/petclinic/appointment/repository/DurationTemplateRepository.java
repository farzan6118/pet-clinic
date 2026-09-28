package com.github.farzan6118.petclinic.appointment.repository;

import com.github.farzan6118.petclinic.appointment.model.DurationTemplate;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DurationTemplateRepository extends JpaRepository<DurationTemplate, Integer> {

    Optional<DurationTemplate> findByUuid(UUID uuid);

    Optional<DurationTemplate> findByUuidAndEntityStatus(UUID uuid, EntityStatus entityStatus);

    Optional<DurationTemplate> findByNameIgnoreCase(String name);

    boolean existsByDurationMinutes(Integer durationMinutes);

    boolean existsByName(String name);

    boolean existsByNameAndUuidNot(String name, UUID uuid);

    boolean existsByDurationMinutesAndUuidNot(Integer duration, UUID uuid);

    Page<DurationTemplate> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Optional<DurationTemplate> findByDurationMinutes(Integer durationMinutes);
}
