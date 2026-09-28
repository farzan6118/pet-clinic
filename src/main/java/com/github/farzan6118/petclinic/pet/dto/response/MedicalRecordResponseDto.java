package com.github.farzan6118.petclinic.pet.dto.response;

import com.github.farzan6118.petclinic.common.enums.MedicalRecordType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MedicalRecordResponseDto(
        UUID uuid,
        UUID petUuid,
        String petName,
        UUID visitUuid,
        UUID vetUuid,
        String vetFullName,
        MedicalRecordType type,
        String title,
        String diagnosis,
        String clinicalNotes,
        String treatmentPlan,
        String prescription,
        boolean followUpRequired,
        LocalDate followUpDate,
        String vaccinationDetails,
        String surgeryDetails,
        Instant createdDate
) {
}
