package com.github.farzan6118.clinic.repository;

import com.github.farzan6118.clinic.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClinicRepository extends JpaRepository<Building, Integer> {

    Optional<Building> findByUuid(UUID uuid);

    Optional<Building> findFirstByActive(boolean active);

    boolean existsByCode(int code);

    boolean existsByCodeAndUuidNot(int code, UUID uuid);
}
