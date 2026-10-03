package com.github.farzan6118.pet.repository;

import com.github.farzan6118.pet.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByUuid(UUID petUuid);

    Optional<MedicalRecord> findByPetUuid(UUID petUuid);
}
