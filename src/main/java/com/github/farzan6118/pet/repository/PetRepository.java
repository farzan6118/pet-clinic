package com.github.farzan6118.pet.repository;

import com.github.farzan6118.pet.model.Pet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, Long> {
    @EntityGraph(attributePaths = {
            "species",
            "owner"
    })
    Optional<Pet> findByUuid(UUID uuid);

    @EntityGraph(attributePaths = {
            "species"
    })
    List<Pet> findByOwnerUuid(UUID ownerUuid);

    boolean existsByNameAndUuid(String name, UUID uuid);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long petId);
}
