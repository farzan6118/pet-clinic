package com.github.farzan6118.pet.repository;

import com.github.farzan6118.pet.model.Pet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pet p where p.uuid = :uuid")
    Optional<Pet> findByUuidForUpdate(@Param("uuid") UUID uuid);

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
