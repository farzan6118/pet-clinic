package com.github.farzan6118.petclinic.appointment.dto.response;

import com.github.farzan6118.petclinic.common.enums.VisitStatus;
import com.github.farzan6118.petclinic.common.enums.VisitType;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitResponseDto(
        UUID uuid,
        UUID petUuid,
        String petName,
        String species,
        String ownerFullName,
        UUID vetUuid,
        String vetFullName,
        LocalDateTime visitDateFrom,
        LocalDateTime visitDateTo,
        VisitType visitType,
        UUID roomUuid,
        String description,
        VisitStatus status
) {
}
