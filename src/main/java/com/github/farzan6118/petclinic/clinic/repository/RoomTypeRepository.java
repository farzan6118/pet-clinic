package com.github.farzan6118.petclinic.clinic.repository;

import com.github.farzan6118.petclinic.clinic.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    Optional<RoomType> findByUuid(UUID uuid);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndUuidNot(String name, UUID uuid);

}
