package com.github.farzan6118.petclinic.pet.repository;

import com.github.farzan6118.petclinic.pet.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpeciesRepository extends JpaRepository<Species, Integer> {

    Optional<Species> findByUuid(UUID uuid);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndUuidNot(String code, UUID uuid);
}
