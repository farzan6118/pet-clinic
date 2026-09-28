package com.github.farzan6118.petclinic.clinic.repository;

import com.github.farzan6118.petclinic.clinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepository extends JpaRepository<Clinic, Integer> {

    Optional<Clinic> findByUuid(UUID uuid);

    Optional<Clinic> findFirstByActive(boolean active);

    boolean existsByCode(int code);

    boolean existsByCodeAndUuidNot(int code, UUID uuid);
}
